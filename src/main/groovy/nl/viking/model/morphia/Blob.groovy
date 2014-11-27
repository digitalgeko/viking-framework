package nl.viking.model.morphia

import com.mongodb.gridfs.GridFSDBFile
import nl.viking.db.GridFSUtils
import org.mongodb.morphia.annotations.Embedded

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 5/17/13
 * Time: 12:04 PM
 * To change this template use File | Settings | File Templates.
 */
@Embedded
class Blob {

    String id

    GridFSDBFile get() {
        GridFSUtils.file(id)
    }

    void set(def src, String fileName = null) {
        id = GridFSUtils.getFileId(src, fileName)
    }

    static create(def src, String fileName = null) {
        def blob = new Blob()
        blob.set(src, fileName)
        return blob
    }
}
