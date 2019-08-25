/*******************************************************************************
 * Copyright (c) 2017 Kichwa Coders Ltd. and others.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package org.eclipse.lsp4e.debug.debugmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.lsp4j.debug.Scope;
import org.eclipse.lsp4j.debug.ScopesArguments;
import org.eclipse.lsp4j.debug.StackFrame;

public class DSPStackFrame extends DSPDebugElement implements IStackFrame {
	private DSPThread thread;
	private StackFrame stackFrame;
	private int depth;
	private IVariable[] cachedVariables;

	public DSPStackFrame(DSPThread thread, StackFrame stackFrame, int depth) {
		super(thread.getDebugTarget());
		this.thread = thread;
		this.stackFrame = stackFrame;
		this.depth = depth;
	}

	public DSPStackFrame replace(StackFrame newStackFrame, int newDepth) {
		if (newDepth == depth && Objects.equals(newStackFrame.getSource(), stackFrame.getSource())) {
			stackFrame = newStackFrame;
			cachedVariables = null;
			return this;
		}
		return new DSPStackFrame(thread, newStackFrame, newDepth);
	}

	@Override
	public void terminate() throws DebugException {
		getDebugTarget().terminate();
	}

	@Override
	public boolean isTerminated() {
		return getDebugTarget().isTerminated();
	}

	@Override
	public boolean canTerminate() {
		return getDebugTarget().canTerminate();
	}

	@Override
	public void suspend() throws DebugException {
		getThread().suspend();
	}

	@Override
	public void resume() throws DebugException {
		getThread().resume();
	}

	@Override
	public boolean isSuspended() {
		return getDebugTarget().isSuspended();
	}

	@Override
	public boolean canSuspend() {
		return getThread().canSuspend();
	}

	@Override
	public boolean canResume() {
		return getThread().canResume();
	}

	@Override
	public void stepReturn() throws DebugException {
		getThread().stepReturn();
	}

	@Override
	public void stepOver() throws DebugException {
		getThread().stepOver();
	}

	@Override
	public void stepInto() throws DebugException {
		getThread().stepInto();
	}

	@Override
	public boolean isStepping() {
		return getThread().isStepping();
	}

	@Override
	public boolean canStepReturn() {
		return getThread().canStepReturn();
	}

	@Override
	public boolean canStepOver() {
		return getThread().canStepOver();
	}

	@Override
	public boolean canStepInto() {
		return getThread().canStepInto();
	}

	@Override
	public boolean hasVariables() throws DebugException {
		return true;
	}

	@Override
	public boolean hasRegisterGroups() throws DebugException {
		return false;
	}

	@Override
	public IVariable[] getVariables() throws DebugException {
		if (cachedVariables == null) {
			ScopesArguments arguments = new ScopesArguments();
			arguments.setFrameId(stackFrame.getId());
			Scope[] scopes = complete(getDebugTarget().getDebugProtocolServer().scopes(arguments)).getScopes();
			List<DSPVariable> vars = new ArrayList<>();
			for (Scope scope : scopes) {
				DSPVariable variable = new DSPVariable(getDebugTarget(), Long.valueOf(-1), scope.getName(), "",
						scope.getVariablesReference());
				vars.add(variable);
			}
			cachedVariables = vars.toArray(new IVariable[vars.size()]);
		}
		return cachedVariables;
	}

	@Override
	public DSPThread getThread() {
		return thread;
	}

	@Override
	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		return null;
	}

	@Override
	public String getName() throws DebugException {
		return stackFrame.getName();
	}

	@Override
	public int getLineNumber() throws DebugException {
		return (int) (long) stackFrame.getLine();
	}

	@Override
	public int getCharStart() throws DebugException {
		return -1;
	}

	@Override
	public int getCharEnd() throws DebugException {
		return -1;
	}

	public String getSourceName() {
		return stackFrame.getSource().getPath();
	}

	public Long getFrameId() {
		return stackFrame.getId();
	}

	@Override
	public String toString() {
		return "StackFrame [depth=" + depth + ", line=" + stackFrame.getLine() + ", thread=" + thread + ", stackFrame="
				+ stackFrame + "]";
	}

}
