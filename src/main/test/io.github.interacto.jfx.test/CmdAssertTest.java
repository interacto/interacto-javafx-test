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
import io.github.interacto.command.CommandImpl;
import io.github.interacto.jfx.binding.JfxWidgetBinding;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CmdAssertTest {
	JfxWidgetBinding<?, ?, ?> binding;

	@BeforeEach
	void setUp() {
		binding = Mockito.mock(JfxWidgetBinding.class);
	}

	@Test
	void testOfTypeOK() {
		final CmdAssert<?> cmdAssert = new CmdAssert<>(Map.entry(new StubCmd3(), binding));
		cmdAssert.ofType(Command.class);
		cmdAssert.ofType(CommandImpl.class);
		cmdAssert.ofType(StubCmd3.class);
		cmdAssert.ofType(StubCmd1.class);
	}

	@Test
	void testOfTypeKO() {
		final CmdAssert<?> cmdAssert = new CmdAssert<>(Map.entry(new StubCmd3(), binding));
		assertThrows(AssertionError.class, () -> cmdAssert.ofType(StubCmd2.class));
	}

	@Test
	void testOfTypeKO2() {
		final CmdAssert<?> cmdAssert = new CmdAssert<>(Map.entry(new StubCmd1(), binding));
		assertThrows(AssertionError.class, () -> cmdAssert.ofType(StubCmd3.class));
	}


	public static class StubCmd1 extends CommandImpl {
		@Override
		protected void doCmdBody() {
		}
	}
	public static class StubCmd2 extends CommandImpl {
		@Override
		protected void doCmdBody() {
		}
	}
	public static class StubCmd3 extends StubCmd1 {
	}
}
