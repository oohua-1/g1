package h2.fw.core.web.aspects;

public class AllureAttachmentUtil {
    private static ThreadLocal<Boolean> isAttachmentNeeded = ThreadLocal.withInitial(() -> false);
    private static ThreadLocal<String> attachmentTitle = ThreadLocal.withInitial(() -> "");
    private static ThreadLocal<String> attachmentContent = ThreadLocal.withInitial(() -> "");
    private static ThreadLocal<String> attachmentType = ThreadLocal.withInitial(() -> "text/html");

    public static boolean isAttachmentNeeded() {
        return isAttachmentNeeded.get();
    }

    public static void setAttachmentNeeded(boolean needed, String title, String content, String type) {
        isAttachmentNeeded.set(needed);
        attachmentTitle.set(title);
        attachmentContent.set(content);
        attachmentType.set(type);
    }

    public static void clearAttachment() {
        isAttachmentNeeded.remove();
        attachmentTitle.remove();
        attachmentContent.remove();
        attachmentType.remove();
    }

    public static String getAttachmentTitle() {
        return attachmentTitle.get();
    }

    public static String getAttachmentContent() {
        return attachmentContent.get();
    }

    public static String getAttachmentType() {
        return attachmentType.get();
    }
}
