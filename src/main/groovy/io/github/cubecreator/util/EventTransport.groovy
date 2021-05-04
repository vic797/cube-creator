package io.github.cubecreator.util

class EventTransport {

    public static final String EVENT_OPEN_MARKDOWN = "event-open-markdown"
    public static final String EVENT_OPEN_FILE = "event-open-file"
    public static final String EVENT_OPEN_PACK = "event-open-pack"

    private final Object[] params
    private final String event

    EventTransport(String event, Object... params) {
        this.event = event
        this.params = params
    }

    String getEvent() {
        return event
    }

    Object[] getParams() {
        return params
    }
}
