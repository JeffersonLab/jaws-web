class AlarmClass {
    constructor(name, priority, location, category, rationale, correctiveaction, pointofcontactusername, filterable,
                latching, ondelayseconds, offdelayseconds, maskedby, screenpath) {
        this.name = name;
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
        this.screenpath = screenpath;
    }
}

class AlarmRegistration {
    constructor(name, clazz, priority, location, category, rationale, correctiveaction, pointofcontactusername,
                filterable, latching, ondelayseconds, offdelayseconds, maskedby, screenpath, epicspv) {
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
        this.screenpath = screenpath;
        this.epicspv = epicspv;
    }
}

class EffectiveRegistration {
    constructor(name) {
        this.name = name;
    }
}

class KafkaLogPosition {
    constructor(name, position) {
        this.name = name;
        this.position = position;
    }
}

export {AlarmClass, AlarmRegistration, EffectiveRegistration, KafkaLogPosition}