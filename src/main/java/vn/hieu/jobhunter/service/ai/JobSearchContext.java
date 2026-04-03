package vn.hieu.jobhunter.service.ai;

import vn.hieu.jobhunter.domain.dto.chat.ChatResponse;
import java.util.List;

public class JobSearchContext {
    private static final ThreadLocal<List<ChatResponse.JobLink>> context = new ThreadLocal<>();

    public static void setJobs(List<ChatResponse.JobLink> jobs) {
        context.set(jobs);
    }

    public static List<ChatResponse.JobLink> getJobs() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}
