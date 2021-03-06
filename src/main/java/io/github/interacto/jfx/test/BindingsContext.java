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

import io.github.interacto.command.Command;
import io.github.interacto.jfx.binding.BindingsObserver;
import io.github.interacto.jfx.binding.JfxWidgetBinding;
import io.reactivex.disposables.Disposable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.assertj.core.api.ListAssert;
import org.assertj.core.internal.Failures;

public class BindingsContext {
	private final Set<JfxWidgetBinding<?, ?, ?>> bindings;
	private final List<Disposable> disposables;
	private final List<Map.Entry<Command, JfxWidgetBinding<?, ?, ?>>> commands;
	protected final BindingsObserver observer;

	public BindingsContext() {
		super();
		bindings = new HashSet<>();
		disposables = new ArrayList<>();
		commands = new ArrayList<>();
		observer = new BindingsObserverImpl();
	}

	private List<Command> getCommands() {
		return commands.stream().map(entry -> entry.getKey()).collect(Collectors.toList());
	}

	private boolean isClassMatchesCommandType(final Class<?> cmdCl, final Map.Entry<Command, JfxWidgetBinding<?, ?, ?>> entry) {
		return cmdCl.isInstance(entry.getKey());
	}

	public <C extends Command> CmdAssert<C> oneCmdProduced(final Class<C> clCmd) {
		if(commands.size() != 1) {
			throw Failures.instance().failure("We registered " + commands.size() + " produced commands instead of a single one: " +
				getCommands());
		}
		if(!isClassMatchesCommandType(clCmd, commands.get(0))) {
			throw Failures.instance().failure("The produced command is of type " + commands.get(0).getClass().getName() +
				" while a command of type " + clCmd.getName() + " is expected.");
		}
		return new CmdAssert<>((Map.Entry<C, JfxWidgetBinding<?, ?, ?>>) commands.get(0));
	}

	public <C extends Command> CmdAssert<C> oneCmdProducedAmong(final Class<C> clCmd) {
		final var result = commands
			.stream()
			.filter(cmd -> isClassMatchesCommandType(clCmd, cmd))
			.collect(Collectors.toList());

		if(result.size() != 1) {
			throw Failures.instance().failure("One command of type " + clCmd.getName() +
				" expected, but " + result.size() + " found: " + result);
		}

		return new CmdAssert<>((Map.Entry<C, JfxWidgetBinding<?, ?, ?>>) result.get(0));
	}

	public ListAssert<CmdAssert<?>> listAssert() {
		return AssertionsForInterfaceTypes.assertThat(commands.stream().map(cmd -> new CmdAssert<>(cmd)).collect(Collectors.toList()));
	}

	public <C extends Command> BindingsContext oneCmdProduced(final Class<C> clCmd, final Consumer<C> cmdChecker) {
		oneCmdProduced(clCmd);
		cmdChecker.accept((C) commands.get(0).getKey());
		return this;
	}

	public BindingsContext cmdsProduced(final int nbCmds) {
		if(commands.size() != nbCmds) {
			throw Failures.instance().failure("We collected " + commands.size() + " produced commands instead of " +
				nbCmds + ": " + getCommands());
		}
		return this;
	}

	public BindingsContext noCmdProduced() {
		if(!commands.isEmpty()) {
			throw Failures.instance().failure("We registered " + commands.size() + " produced commands instead of zero: " + getCommands());
		}
		return this;
	}

	public BindingsContext hasBindings(final int number) {
		if(bindings.size() != number) {
			throw Failures.instance().failure("The number of existing bindings is " + bindings.size() + " instead of " + number + ".");
		}
		return this;
	}

	private class BindingsObserverImpl implements BindingsObserver {
		@Override
		public void observeBinding(final JfxWidgetBinding<?, ?, ?> binding) {
			if(binding == null) {
				throw Failures.instance().failure("The given widget binding is null.");
			}

			bindings.add(binding);
			disposables.add(binding.produces().subscribe(cmd -> commands.add(Map.entry(cmd, binding))));
		}

		@Override
		public void clearObservedBindings() {
			disposables.forEach(d -> d.dispose());
			disposables.clear();
			bindings.forEach(b -> b.uninstallBinding());
			bindings.clear();
		}
	}
}
