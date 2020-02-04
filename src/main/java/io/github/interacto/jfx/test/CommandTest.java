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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class CommandTest<C extends Command> {
	protected C cmd;
	protected int nbExec;

	protected abstract Stream<Runnable> cannotDoConfigurations();

	protected abstract Stream<Runnable> canDoConfigurations();

	protected abstract Runnable doChecker();

	protected abstract Runnable undoChecker();

	@BeforeEach
	protected void setUpCommand() {
		nbExec = 0;
	}

	@AfterEach
	protected void tearDownCommand() {
		cmd.flush();
		cmd = null;
	}

	@ParameterizedTest
	@MethodSource("cannotDoConfigurations")
	protected void testCannotDo(final Runnable config) {
		config.run();
		assertThat(cmd.canDo()).isFalse();
	}

	@ParameterizedTest
	@MethodSource("canDoConfigurations")
	protected void testCanDo(final Runnable config) {
		config.run();
		assertThat(cmd.canDo()).isTrue();
	}

	@Test
	protected void testDo() {
		canDoConfigurations().collect(Collectors.toList()).get(0).run();
		cmd.doIt();
		cmd.done();
		nbExec = 1;
		doChecker().run();
	}
}
