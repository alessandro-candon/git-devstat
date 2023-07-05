package org.devstat.gitdevstat.support;

import org.devstat.gitdevstat.dto.JobResult;

public interface IWorkerThreadJob {
    public JobResult processCommand();
}
