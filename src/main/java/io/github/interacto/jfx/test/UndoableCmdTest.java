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

import com.google.common.collect.Streams;
import io.github.interacto.command.Command;
import io.github.interacto.undo.Undoable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class UndoableCmdTest<C extends Command & Undoable> extends CommandTest<C> {
	protected ResourceBundle bundle;

	@BeforeEach
	void setUpUndoableCmdTest() {
		bundle = Mockito.mock(ResourceBundle.class);
	}

	protected abstract Stream<Runnable> undoCheckers();

	protected Stream<Arguments> undoProvider() {
		final List<Runnable> canDos = canDoFixtures().collect(Collectors.toList());
		final List<Runnable> oracles = undoCheckers().collect(Collectors.toList());

		if(canDos.size() == oracles.size()) {
			return Streams.zip(canDos.stream(), oracles.stream(), (a, b) -> Arguments.of(a, b));
		}
		if(oracles.size() != 1) {
			fail("Incorrect number of undo oracles: either the same number of cando fixtures or a single oracle");
		}

		final Runnable oracle = oracles.get(0);
		return canDos.stream().map(cando -> Arguments.arguments(cando, oracle));
	}

	@ParameterizedTest
	@MethodSource("undoProvider")
	protected void testUndo(final Runnable fixture, final Runnable undoOracle) {
		fixture.run();
		cmd.doIt();
		cmd.done();
		nbExec = 1;
		cmd.undo();
		undoOracle.run();
	}

	@ParameterizedTest
	@MethodSource("doProvider")
	protected void testRedo(final Runnable fixture, final Runnable oracle) {
		fixture.run();
		cmd.doIt();
		cmd.done();
		cmd.undo();
		cmd.redo();
		nbExec = 2;
		oracle.run();
	}

	@ParameterizedTest
	@MethodSource("undoProvider")
	protected void testUndo2Times(final Runnable fixture, final Runnable undoOracle) {
		fixture.run();
		cmd.doIt();
		cmd.done();
		cmd.undo();
		cmd.redo();
		cmd.undo();
		nbExec = 2;
		undoOracle.run();
	}

	@ParameterizedTest
	@MethodSource("doProvider")
	protected void testRedo2Times(final Runnable fixture, final Runnable oracle) {
		fixture.run();
		cmd.doIt();
		cmd.done();
		cmd.undo();
		cmd.redo();
		cmd.undo();
		cmd.redo();
		nbExec = 3;
		oracle.run();
	}

	@ParameterizedTest
	@MethodSource("canDoFixtures")
	protected void testUndoName(final Runnable fixture) {
		fixture.run();
		assertThat(cmd.getUndoName(bundle)).isNotEmpty();
	}
}
