package org.hive2hive.processes.framework.abstracts;

import java.util.UUID;

import org.hive2hive.processes.framework.ProcessState;
import org.hive2hive.processes.framework.exceptions.InvalidProcessStateException;
import org.hive2hive.processes.framework.interfaces.IProcessComponent;
import org.hive2hive.processes.framework.interfaces.IProcessContext;

public abstract class ProcessComponent implements IProcessComponent {

	private final String id;
	private double progress;
	private ProcessState state;
	protected IProcessContext context;
	
	private boolean isRollbacking;
	
	protected ProcessComponent() {
		this.id = generateID();
		this.progress = 0.0;
		this.state = ProcessState.READY;
	}
	
	@Override
	public final void start() throws InvalidProcessStateException {
		if (state != ProcessState.READY){
			throw new InvalidProcessStateException(state);
		}
		state = ProcessState.RUNNING;
		isRollbacking = false;
		doExecute();
	}

	@Override
	public final void pause() throws InvalidProcessStateException {
		if (state != ProcessState.RUNNING || state != ProcessState.ROLLBACKING) {
			throw new InvalidProcessStateException(state);
		}
		state = ProcessState.PAUSED;
		doPause();
	}

	@Override
	public final void resume() throws InvalidProcessStateException {
		if (state != ProcessState.PAUSED) {
			throw new InvalidProcessStateException(state);
		}
		if (isRollbacking){
			state = ProcessState.RUNNING;
		} else {
			state = ProcessState.ROLLBACKING;
		}
		doResume(); // TODO ensure this hook method takes correct resume (running or rollback)
	}

	@Override
	public final void cancel() throws InvalidProcessStateException {
		if (state != ProcessState.RUNNING || state != ProcessState.PAUSED){
			throw new InvalidProcessStateException(state);
		}
		state = ProcessState.ROLLBACKING;
		isRollbacking = true;
		doRollback();
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public double getProgress() {
		return progress;
	}

	@Override
	public ProcessState getState() {
		return state;
	}

	@Override
	public abstract void join();
	
	protected abstract void doExecute();
	
	protected abstract void doPause();
	
	protected abstract void doResume();
	
	protected abstract void doRollback();
	
	@Override
	public boolean equals(Object obj) {
	    if (obj == null)
	        return false;
	    if (obj == this)
	        return true;
	    if (!(obj instanceof ProcessComponent))
	        return false;
	
	    ProcessComponent other = (ProcessComponent) obj;
	    return id.equals(other.getID());
	}

	@Override
	public int hashCode() {
		int hash = 7;
        return 31 * hash + id.hashCode();
    }

	private static String generateID() {
		return UUID.randomUUID().toString();
	}

}
