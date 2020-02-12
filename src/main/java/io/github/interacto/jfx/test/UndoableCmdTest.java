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
import io.github.interacto.undo.Undoable;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class UndoableCmdTest<C extends Command & Undoable> extends CommandTest<C> {
	protected ResourceBundle bundle;

	@BeforeEach
	void setUpUndoableCmdTest() {
		bundle = Mockito.mock(ResourceBundle.class);
	}

	protected abstract Runnable undoChecker();

	@Test
	protected void testUndo() {
		canDoFixtures().collect(Collectors.toList()).get(0).run();
		cmd.doIt();
		cmd.done();
		nbExec = 1;
		cmd.undo();
		undoChecker().run();
	}

	@Test
	protected void testRedo() {
		canDoFixtures().collect(Collectors.toList()).get(0).run();
		cmd.doIt();
		cmd.done();
		cmd.undo();
		cmd.redo();
		nbExec = 2;
		doChecker().run();
	}

	@Test
	protected void testUndo2Times() {
		canDoFixtures().collect(Collectors.toList()).get(0).run();
		cmd.doIt();
		cmd.done();
		cmd.undo();
		cmd.redo();
		cmd.undo();
		nbExec = 2;
		undoChecker().run();
	}

	@Test
	protected void testRedo2Times() {
		canDoFixtures().collect(Collectors.toList()).get(0).run();
		cmd.doIt();
		cmd.done();
		cmd.undo();
		cmd.redo();
		cmd.undo();
		cmd.redo();
		nbExec = 3;
		doChecker().run();
	}

	@Test
	protected void testUndoName() {
		canDoFixtures().collect(Collectors.toList()).get(0).run();
		assertThat(cmd.getUndoName(bundle)).isNotEmpty();
	}
}
