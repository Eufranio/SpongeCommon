/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.command.brigadier.tree;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.command.parameter.managed.ValueCompleter;
import org.spongepowered.common.command.brigadier.argument.parser.ArgumentParser;
import org.spongepowered.common.command.brigadier.argument.parser.StandardArgumentParser;
import org.spongepowered.common.command.parameter.SpongeParameterKey;

// We use the ArgumentBuilder primarily for setting redirects properly.
public class SpongeArgumentCommandNodeBuilder<T> extends ArgumentBuilder<CommandSource, SpongeArgumentCommandNodeBuilder<T>> {

    private final SpongeParameterKey<? super T> key;
    private final ArgumentParser<? extends T> type;
    @Nullable private final ValueCompleter completer;

    public SpongeArgumentCommandNodeBuilder(
            final SpongeParameterKey<? super T> key,
            final ArgumentParser<? extends T> type,
            final ValueCompleter completer) {
        this.key = key;
        this.type = type;
        this.completer = type == completer && type instanceof StandardArgumentParser ? null : completer;
    }

    @Override
    protected SpongeArgumentCommandNodeBuilder<T> getThis() {
        return this;
    }

    @Override
    public SpongeArgumentCommandNode<? extends T> build() {
        return new SpongeArgumentCommandNode<>(
                this.key,
                this.type,
                this.completer,
                this.getCommand(),
                this.getRequirement(),
                this.getRedirect(),
                this.getRedirectModifier(),
                this.isFork()
        );
    }
}
