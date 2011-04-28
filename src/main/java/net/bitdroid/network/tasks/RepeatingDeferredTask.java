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

import java.util.concurrent.TimeUnit;

/**
 * @author cdecker
 *
 */
public abstract class RepeatingDeferredTask extends DeferredTask {
	long interval = 0;

	public RepeatingDeferredTask(long delay){
		super(delay);
		interval = delay;
	}

	public RepeatingDeferredTask(long delay, TimeUnit tu){
		super(delay, tu);
		interval = TimeUnit.MILLISECONDS.convert(delay, tu);
	}

	/* (non-Javadoc)
	 * @see net.bitdroid.network.tasks.DeferredTask#cleanup()
	 */
	public void reschedule() {
		this.executionTime = System.currentTimeMillis() + interval;
	}
}
