/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.prestosql.sql.parser;

import io.airlift.log.Logger;
import io.hivesql.presto.sql.parser.HiveSqlBaseLexer;
import io.hivesql.presto.sql.parser.HiveSqlBaseParser;
import io.prestosql.sql.tree.Expression;
import io.prestosql.sql.tree.Node;
import io.prestosql.sql.tree.PathSpecification;
import io.prestosql.sql.tree.Statement;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.DefaultErrorStrategy;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.TerminalNode;

import javax.inject.Inject;

import java.util.*;
import java.util.function.Function;
import io.prestosql.sql.parser.hive.PrestoHiveAstBuilder;

import static java.util.Objects.requireNonNull;

public class SqlParser
{
    private static final Logger LOG = Logger.get(SqlParser.class);

    private static final BaseErrorListener LEXER_ERROR_LISTENER = new BaseErrorListener()
    {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String message, RecognitionException e)
        {
            throw new ParsingException(message, e, line, charPositionInLine);
        }
    };

    private static final ErrorHandler PARSER_ERROR_HANDLER = ErrorHandler.builder()
            .specialRule(SqlBaseParser.RULE_expression, "<expression>")
            .specialRule(SqlBaseParser.RULE_booleanExpression, "<expression>")
            .specialRule(SqlBaseParser.RULE_valueExpression, "<expression>")
            .specialRule(SqlBaseParser.RULE_primaryExpression, "<expression>")
            .specialRule(SqlBaseParser.RULE_identifier, "<identifier>")
            .specialRule(SqlBaseParser.RULE_string, "<string>")
            .specialRule(SqlBaseParser.RULE_query, "<query>")
            .specialRule(SqlBaseParser.RULE_type, "<type>")
            .specialToken(SqlBaseLexer.INTEGER_VALUE, "<integer>")
            .ignoredRule(SqlBaseParser.RULE_nonReserved)
            .build();

    private final EnumSet<IdentifierSymbol> allowedIdentifierSymbols;
    private boolean enhancedErrorHandlerEnabled;

    public SqlParser()
    {
        this(new SqlParserOptions());
    }

    @Inject
    public SqlParser(SqlParserOptions options)
    {
        requireNonNull(options, "options is null");
        allowedIdentifierSymbols = EnumSet.copyOf(options.getAllowedIdentifierSymbols());
        enhancedErrorHandlerEnabled = options.isEnhancedErrorHandlerEnabled();
    }

    /**
     * Consider using {@link #createStatement(String, ParsingOptions)}
     */
    @Deprecated
    public Statement createStatement(String sql)
    {
        return createStatement(sql, new ParsingOptions());
    }

    public Statement createStatement(String sql, ParsingOptions parsingOptions)
    {
        Statement statement = (Statement) invokeParser("statement",
                sql, SqlBaseParser::singleStatement, parsingOptions, "singleStatement");
        return statement;
    }

    /**
     * Consider using {@link #createExpression(String, ParsingOptions)}
     */
    @Deprecated
    public Expression createExpression(String expression)
    {
        LOG.info("from Expression createExpression(String expression)");
        return createExpression(expression, new ParsingOptions());
    }

    public Expression createExpression(String expression, ParsingOptions parsingOptions)
    {
        LOG.info("from Expression createExpression(String expression, ParsingOptions parsingOptions)");
        Expression ex = (Expression) invokeParser("expression", expression,
                SqlBaseParser::standaloneExpression, parsingOptions, "standaloneExpression");
        return ex;
    }

    public PathSpecification createPathSpecification(String expression)
    {
        LOG.info("from PathSpecification createPathSpecification(String expression)");
        PathSpecification plan = (PathSpecification) invokeParser("path specification",
                expression, SqlBaseParser::standalonePathSpecification, new ParsingOptions(), "standalonePathSpecification");
        return plan;
    }

    private Node invokeParser(String name, String sql, Function<SqlBaseParser,
            ParserRuleContext> parseFunction, ParsingOptions parsingOptions, String type)
    {
        LOG.info("sql:" + sql);
        if (!parsingOptions.useHiveParser()) {
            LOG.info("use presto sql");
        } else {
            LOG.info("use hive sql");
            LOG.info("debug for lateral view");
            try {
                HiveSqlBaseLexer lexer =
                        new HiveSqlBaseLexer(
                                new CaseInsensitiveStream(CharStreams.fromString(sql)));
                CommonTokenStream tokenStream = new CommonTokenStream(lexer);
                HiveSqlBaseParser parser =
                        new HiveSqlBaseParser(tokenStream);

                // Override the default error strategy to not attempt inserting or deleting a token.
                // Otherwise, it messes up error reporting
                parser.setErrorHandler(new DefaultErrorStrategy()
                {
                    @Override
                    public Token recoverInline(Parser recognizer)
                            throws RecognitionException
                    {
                        if (nextTokensContext == null) {
                            throw new InputMismatchException(recognizer);
                        }
                        else {
                            throw new InputMismatchException(recognizer, nextTokensState, nextTokensContext);
                        }
                    }
                });

                parser.addParseListener(new PostProcessor(Arrays.asList(parser.getRuleNames())));

                lexer.removeErrorListeners();
                lexer.addErrorListener(LEXER_ERROR_LISTENER);

                parser.removeErrorListeners();

                if (enhancedErrorHandlerEnabled) {
                    parser.addErrorListener(PARSER_ERROR_HANDLER);
                }
                else {
                    parser.addErrorListener(LEXER_ERROR_LISTENER);
                }

                ParserRuleContext tree;
                Function<HiveSqlBaseParser, ParserRuleContext> hiveParseFunction = null;
                if (type.equals("singleStatement")) {
                    hiveParseFunction = HiveSqlBaseParser::singleStatement;
                } else if (type.equals("standaloneExpression")) {
                    hiveParseFunction = HiveSqlBaseParser::standaloneExpression;
                } else {
                    hiveParseFunction = HiveSqlBaseParser::standalonePathSpecification;
                }
                try {
                    // first, try parsing with potentially faster SLL mode
                    parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
                    tree = hiveParseFunction.apply(parser);
                }
                catch (ParseCancellationException ex) {
                    // if we fail, parse with LL mode
                    tokenStream.reset(); // rewind input stream
                    parser.reset();

                    parser.getInterpreter().setPredictionMode(PredictionMode.LL);
                    tree = hiveParseFunction.apply(parser);
                }

                return new PrestoHiveAstBuilder(parsingOptions).visit(tree);
            }
            catch (StackOverflowError e) {
                throw new ParsingException(name + " is too large (stack overflow while parsing)");
            }
        }

        try {
            SqlBaseLexer lexer = new SqlBaseLexer(new CaseInsensitiveStream(CharStreams.fromString(sql)));
            CommonTokenStream tokenStream = new CommonTokenStream(lexer);
            SqlBaseParser parser = new SqlBaseParser(tokenStream);

            // Override the default error strategy to not attempt inserting or deleting a token.
            // Otherwise, it messes up error reporting
            parser.setErrorHandler(new DefaultErrorStrategy()
            {
                @Override
                public Token recoverInline(Parser recognizer)
                        throws RecognitionException
                {
                    if (nextTokensContext == null) {
                        throw new InputMismatchException(recognizer);
                    }
                    else {
                        throw new InputMismatchException(recognizer, nextTokensState, nextTokensContext);
                    }
                }
            });

            parser.addParseListener(new PostProcessor(Arrays.asList(parser.getRuleNames())));

            lexer.removeErrorListeners();
            lexer.addErrorListener(LEXER_ERROR_LISTENER);

            parser.removeErrorListeners();

            if (enhancedErrorHandlerEnabled) {
                parser.addErrorListener(PARSER_ERROR_HANDLER);
            }
            else {
                parser.addErrorListener(LEXER_ERROR_LISTENER);
            }

            ParserRuleContext tree;
            try {
                // first, try parsing with potentially faster SLL mode
                parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
                tree = parseFunction.apply(parser);
            }
            catch (ParseCancellationException ex) {
                // if we fail, parse with LL mode
                tokenStream.reset(); // rewind input stream
                parser.reset();

                parser.getInterpreter().setPredictionMode(PredictionMode.LL);
                tree = parseFunction.apply(parser);
            }

            return new AstBuilder(parsingOptions).visit(tree);
        }
        catch (StackOverflowError e) {
            throw new ParsingException(name + " is too large (stack overflow while parsing)");
        }
    }

    private class PostProcessor
            extends SqlBaseBaseListener
    {
        private final List<String> ruleNames;

        public PostProcessor(List<String> ruleNames)
        {
            this.ruleNames = ruleNames;
        }

        @Override
        public void exitQuotedIdentifier(SqlBaseParser.QuotedIdentifierContext context)
        {
            Token token = context.QUOTED_IDENTIFIER().getSymbol();
            if (token.getText().length() == 2) { // empty identifier
                throw new ParsingException("Zero-length delimited identifier not allowed", null, token.getLine(), token.getCharPositionInLine());
            }
        }

        @Override
        public void exitUnquotedIdentifier(SqlBaseParser.UnquotedIdentifierContext context)
        {
            String identifier = context.IDENTIFIER().getText();
            for (IdentifierSymbol identifierSymbol : EnumSet.complementOf(allowedIdentifierSymbols)) {
                char symbol = identifierSymbol.getSymbol();
                if (identifier.indexOf(symbol) >= 0) {
                    throw new ParsingException("identifiers must not contain '" + identifierSymbol.getSymbol() + "'", null, context.IDENTIFIER().getSymbol().getLine(), context.IDENTIFIER().getSymbol().getCharPositionInLine());
                }
            }
        }

        @Override
        public void exitBackQuotedIdentifier(SqlBaseParser.BackQuotedIdentifierContext context)
        {
            Token token = context.BACKQUOTED_IDENTIFIER().getSymbol();
            throw new ParsingException(
                    "backquoted identifiers are not supported; use double quotes to quote identifiers",
                    null,
                    token.getLine(),
                    token.getCharPositionInLine());
        }

        @Override
        public void exitDigitIdentifier(SqlBaseParser.DigitIdentifierContext context)
        {
            Token token = context.DIGIT_IDENTIFIER().getSymbol();
            throw new ParsingException(
                    "identifiers must not start with a digit; surround the identifier with double quotes",
                    null,
                    token.getLine(),
                    token.getCharPositionInLine());
        }

        @Override
        public void exitNonReserved(SqlBaseParser.NonReservedContext context)
        {
            // we can't modify the tree during rule enter/exit event handling unless we're dealing with a terminal.
            // Otherwise, ANTLR gets confused an fires spurious notifications.
            if (!(context.getChild(0) instanceof TerminalNode)) {
                int rule = ((ParserRuleContext) context.getChild(0)).getRuleIndex();
                throw new AssertionError("nonReserved can only contain tokens. Found nested rule: " + ruleNames.get(rule));
            }

            // replace nonReserved words with IDENT tokens
            context.getParent().removeLastChild();

            Token token = (Token) context.getChild(0).getPayload();
            context.getParent().addChild(new CommonToken(
                    new Pair<>(token.getTokenSource(), token.getInputStream()),
                    SqlBaseLexer.IDENTIFIER,
                    token.getChannel(),
                    token.getStartIndex(),
                    token.getStopIndex()));
        }
    }
}
