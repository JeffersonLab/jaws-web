class AlarmClass {
    constructor(name) {
        this.name = name;
    }
}

class AlarmRegistration {
    constructor(name) {
        this.name = name;
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