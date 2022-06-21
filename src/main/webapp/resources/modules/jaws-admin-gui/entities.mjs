class AlarmActivation {
    constructor(name, error, note, sevr, stat) {
        this.name = name;
        this.error = error;
        this.note = note;
        this.sevr = sevr;
        this.stat = stat;
    }
}

class AlarmCategory {
    constructor(name) {
        this.name = name;
    }
}

class AlarmClass {
    constructor(name, priority, category, rationale, action, contact, filterable,
                latching, ondelay, offdelay) {
        this.name = name;
        this.priority = priority;
        this.category = category;
        this.rationale = rationale;
        this.action = action;
        this.contact = contact;
        this.filterable = filterable;
        this.latching = latching;
        this.ondelay = ondelay;
        this.offdelay = offdelay;
    }
}

class AlarmInstance {
    constructor(name, clazz, location, maskedby, screencommand, epicspv) {
        this.name = name;
        this.class = clazz;
        this.location = location;
        this.maskedby = maskedby;
        this.screencommand = screencommand;
        this.epicspv = epicspv;
    }
}

class AlarmLocation {
    constructor(name, parent) {
        this.name = name;
        this.parent = parent;
    }
}

class AlarmOverride {
    constructor(name, comments, expiration, filtername, oneshot, reason) {
        this.name = name;
        this.comments = comments;
        this.expiration = expiration;
        this.filtername = filtername;
        this.oneshot = oneshot;
        this.reason = reason;
    }
}

class EffectiveAlarm {
    constructor(name, priority, category, rationale, action, contact, filterable, latching,
                ondelay, offdelay, clazz, location, maskedby, screencommand, epicspv, state) {
        this.name = name;

        this.priority = priority;
        this.category = category;
        this.rationale = rationale;
        this.action = action;
        this.contact = contact;
        this.filterable = filterable;
        this.latching = latching;
        this.ondelay = ondelay;
        this.offdelay = offdelay;

        this.class = clazz;
        this.location = location;
        this.maskedby = maskedby;
        this.screencommand = screencommand;
        this.epicspv = epicspv;

        this.state = state;
    }
}

class EffectiveNotification {
    constructor(name, state, disabled_comments, ondelayed_expiration, offdelayed_expiration, filtered_filtername,
                shelved_oneshot, shelved_reason, shelved_comments, latched, masked, error, note, sevr, stat) {
        this.name = name;
        this.state = state;
        this.disabled_comments = disabled_comments;
        this.ondelayed_expiration = ondelayed_expiration;
        this.offdelayed_expiration = offdelayed_expiration;
        this.filtered_filtername = filtered_filtername;
        this.shevled_oneshot = shelved_oneshot;
        this.shelved_reason = shelved_reason;
        this.shevled_comments = shelved_comments;
        this.latched = latched;
        this.masked = masked;
        this.error = error;
        this.note = note;
        this.sevr = sevr;
        this.stat = stat;
    }
}

class EffectiveRegistration {
    constructor(name, clazz, priority, location, category, rationale, action, contact,
                filterable, latching, ondelay, offdelay, maskedby, screencommand, epicspv) {
        this.name = name;
        this.class = clazz;
        this.priority = priority;
        this.location = location;
        this.category = category;
        this.rationale = rationale;
        this.action = action;
        this.contact = contact;
        this.filterable = filterable;
        this.latching = latching;
        this.ondelay = ondelay;
        this.offdelay = offdelay;
        this.maskedby = maskedby;
        this.screencommand = screencommand;
        this.epicspv = epicspv;
    }
}

class KafkaLogPosition {
    constructor(name, position) {
        this.name = name;
        this.position = position;
    }
}

export {AlarmActivation, AlarmCategory, AlarmClass, AlarmInstance, AlarmLocation, AlarmOverride, EffectiveAlarm, EffectiveNotification, EffectiveRegistration, KafkaLogPosition}