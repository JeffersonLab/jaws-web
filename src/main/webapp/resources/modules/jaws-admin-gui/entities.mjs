class AlarmCategory {
    constructor(name) {
        this.name = name;
    }
}

class AlarmClass {
    constructor(name, priority, category, rationale, correctiveaction, pointofcontactusername, filterable,
                latching, ondelayseconds, offdelayseconds) {
        this.name = name;
        this.priority = priority;
        this.category = category;
        this.rationale = rationale;
        this.correctiveaction = correctiveaction;
        this.pointofcontactusername = pointofcontactusername;
        this.filterable = filterable;
        this.latching = latching;
        this.ondelayseconds = ondelayseconds;
        this.offdelayseconds = offdelayseconds;
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

class EffectiveRegistration {
    constructor(name, clazz, priority, location, category, rationale, correctiveaction, pointofcontactusername,
                filterable, latching, ondelayseconds, offdelayseconds, maskedby, screencommand, epicspv) {
        this.name = name;
        this.class = clazz;
        this.priority = priority;
        this.location = location;
        this.category = category;
        this.rationale = rationale;
        this.correctiveaction = correctiveaction;
        this.pointofcontactusername = pointofcontactusername;
        this.filterable = filterable;
        this.latching = latching;
        this.ondelayseconds = ondelayseconds;
        this.offdelayseconds = offdelayseconds;
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

export {AlarmCategory, AlarmClass, AlarmInstance, AlarmLocation, EffectiveRegistration, KafkaLogPosition}