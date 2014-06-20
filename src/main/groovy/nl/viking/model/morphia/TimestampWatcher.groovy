package nl.viking.model.morphia

import com.google.code.morphia.annotations.PrePersist

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 6/12/13
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */

class TimestampWatcher {

    @PrePersist void prePersist(Model model) {
        if (!model._created) model._created = new Date().getTime();
        model._updated = new Date().getTime();
    }

}
