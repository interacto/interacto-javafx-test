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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class CommandTest<C extends Command> {
	protected C cmd;
	protected int nbExec;

	protected Stream<Runnable> cannotDoFixtures() {
		return Stream.of(() -> {
			cmd = (C) Mockito.mock(Command.class);
			Mockito.when(cmd.canDo()).thenReturn(false);
		});
	}

	protected void commonCannotDoFixtures() {
	}

	protected abstract Stream<Runnable> canDoFixtures();

	protected void commonCanDoFixture() {
	}

	protected abstract Stream<Runnable> doCheckers();

	protected void commonDoCheckers() {
	}

	protected Stream<Arguments> doProvider() {
		final List<Runnable> canDos = canDoFixtures().collect(Collectors.toList());
		final List<Runnable> oracles = doCheckers().collect(Collectors.toList());

		if(canDos.size() == oracles.size()) {
			return Streams.zip(canDos.stream(), oracles.stream(), (a, b) -> Arguments.of(a, b));
		}
		if(oracles.size() != 1) {
			fail("Incorrect number of oracles: either the same number of cando fixtures or a single oracle");
		}

		final Runnable oracle = oracles.get(0);
		return canDos.stream().map(cando -> Arguments.arguments(cando, oracle));
	}

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
	@MethodSource("cannotDoFixtures")
	protected void testCannotDo(final Runnable fixture) {
		commonCannotDoFixtures();
		fixture.run();
		assertThat(cmd.canDo()).isFalse();
		assertThat(cmd.hadEffect()).isFalse();
	}

	@ParameterizedTest
	@MethodSource("canDoFixtures")
	protected void testCanDo(final Runnable fixture) {
		commonCanDoFixture();
		fixture.run();
		assertThat(cmd.canDo()).isTrue();
		assertThat(cmd.hadEffect()).isFalse();
	}

	@ParameterizedTest
	@MethodSource("doProvider")
	protected void testDo(final Runnable fixture, final Runnable oracle) {
		commonCanDoFixture();
		fixture.run();
		cmd.doIt();
		cmd.done();
		nbExec = 1;
		commonDoCheckers();
		oracle.run();
	}
}
