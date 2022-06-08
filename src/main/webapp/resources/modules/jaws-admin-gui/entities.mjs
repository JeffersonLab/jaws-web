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
    constructor(name) {
        this.name = name;
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

class EffectiveAlarm {
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

export {AlarmActivation, AlarmCategory, AlarmClass, AlarmInstance, AlarmLocation, AlarmOverride, EffectiveRegistration, KafkaLogPosition}