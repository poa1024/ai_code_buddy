package io.github.poa1024.util;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class NotificationUtils {

    public static void notifyWarning(Project project, String content) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("GptNotificationGroup")
                .createNotification(content, NotificationType.WARNING)
                .notify(project);
    }

    public static void notifyError(Project project, String content) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("GptNotificationGroup")
                .createNotification(content, NotificationType.ERROR)
                .notify(project);
    }
}
