/**
 * Copyright 2011 Christian Decker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file is part the BitDroidNetwork Project.
 */
package net.bitdroid.network.tasks;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author cdecker
 *
 */
public abstract class DeferredTask implements Delayed {
	protected long executionTime = 0;

	public DeferredTask(long delay){
		this.executionTime = System.currentTimeMillis() + delay;
	}

	public DeferredTask(long delay, TimeUnit tu){
		this.executionTime = System.currentTimeMillis() +
			TimeUnit.MILLISECONDS.convert(delay, tu);
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Delayed arg0) {
		return (int)(this.getDelay(TimeUnit.SECONDS) - arg0.getDelay(TimeUnit.SECONDS));
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Delayed#getDelay(java.util.concurrent.TimeUnit)
	 */
	public long getDelay(TimeUnit unit) {
		long now = unit.convert(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
		long exec = unit.convert(executionTime, TimeUnit.MILLISECONDS);
		return exec - now;
	}

	public abstract void execute();
}
