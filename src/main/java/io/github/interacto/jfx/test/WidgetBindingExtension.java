/*
 * Interacto
 * Copyright (C) 2020 Arnaud Blouin
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.interacto.jfx.test;

import io.github.interacto.command.CommandsRegistry;
import io.github.interacto.error.ErrorCatcher;
import io.github.interacto.jfx.binding.Bindings;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class WidgetBindingExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {
	private BindingsAssert bindingsAssert;

	@Override
	public void beforeEach(final ExtensionContext extCtx) {
		bindingsAssert = new BindingsAssert();
		Bindings.setBindingObserver(bindingsAssert.observer);
	}

	@Override
	public void afterEach(final ExtensionContext extCtx) {
		Bindings.setBindingObserver(null);
		bindingsAssert.observer.clearObservedBindings();
		bindingsAssert = null;
		CommandsRegistry.getInstance().clear();
		CommandsRegistry.setInstance(new CommandsRegistry());
		ErrorCatcher.setInstance(new ErrorCatcher());
	}

	@Override
	public boolean supportsParameter(final ParameterContext paramCtx, final ExtensionContext extCtx) throws ParameterResolutionException {
		return paramCtx.getParameter().getType().isInstance(bindingsAssert);
	}

	@Override
	public Object resolveParameter(final ParameterContext paramCtx, final ExtensionContext extCtx) throws ParameterResolutionException {
		return bindingsAssert;
	}
}
