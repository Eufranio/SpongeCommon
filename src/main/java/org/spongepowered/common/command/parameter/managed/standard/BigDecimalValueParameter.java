package org.spongepowered.common.command.parameter.managed.standard;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.command.exception.ArgumentParseException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.ValueCompleter;
import org.spongepowered.api.command.parameter.managed.ValueParser;
import org.spongepowered.api.command.parameter.managed.standard.CatalogedValueParameter;
import org.spongepowered.api.text.Text;
import org.spongepowered.common.command.brigadier.argument.parser.CatalogedArgumentParser;
import org.spongepowered.common.command.brigadier.argument.parser.CustomArgumentParser;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class BigDecimalValueParameter extends CatalogedArgumentParser<BigDecimal> implements CatalogedValueParameter<BigDecimal> {

    private final CatalogKey key = CatalogKey.sponge("big_decimal");

    @Override
    @NonNull
    public CatalogKey getKey() {
        return this.key;
    }

    @Override
    @NonNull
    public List<String> complete(@NonNull final CommandContext context) {
        return ImmutableList.of();
    }

    @Override
    @NonNull
    public Optional<? extends BigDecimal> getValue(
            final Parameter.@NonNull Key<? super BigDecimal> parameterKey,
            final ArgumentReader.@NonNull Mutable reader,
            final CommandContext.@NonNull Builder context) throws ArgumentParseException {
        final String result = reader.parseString();
        try {
            return Optional.of(new BigDecimal(result));
        } catch (final NumberFormatException ex) {
            throw reader.createException(Text.of(ex));
        }
    }

}
