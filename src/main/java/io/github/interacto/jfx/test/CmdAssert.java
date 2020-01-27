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
import io.github.interacto.jfx.binding.JfxWidgetBinding;
import java.util.Map;
import org.assertj.core.internal.Failures;

public class CmdAssert<C extends Command> {
	private final Map.Entry<C, JfxWidgetBinding<?, ?, ?>> command;

	public CmdAssert(final Map.Entry<C, JfxWidgetBinding<?, ?, ?>> tuple) {
		super();
		this.command = tuple;
	}

	public CmdAssert<C> producedBy(final JfxWidgetBinding<?, ?, ?> binding) {
		if(command.getValue() != binding) {
			throw Failures.instance().failure("The expected binding is " + binding + " but " + command.getValue() + " was used.");
		}
		return this;
	}

	public <C2 extends Command> CmdAssert<C2> ofType(final Class<C2> clCmd) {
		if(!clCmd.isInstance(command.getKey())) {
			throw Failures.instance().failure("The produced command is of type " + command.getKey().getClass().getName() +
				" while a command of type " + clCmd.getName() + " is expected.");
		}
		return new CmdAssert<>(Map.entry((C2) command.getKey(), command.getValue()));
	}

	public JfxWidgetBinding<?, ?, ?> getBinding() {
		return command.getValue();
	}

	public C getCommand() {
		return command.getKey();
	}
}
