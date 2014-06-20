package nl.viking.db

import com.liferay.portal.kernel.util.MimeTypesUtil
import com.mongodb.gridfs.GridFS
import com.mongodb.gridfs.GridFSDBFile
import com.mongodb.gridfs.GridFSInputFile
import org.bson.types.ObjectId

/**
 * Created with IntelliJ IDEA.
 * User: mardo
 * Date: 4/10/13
 * Time: 11:46 AM
 * To change this template use File | Settings | File Templates.
 */
class GridFSUtils {
    static def gridfs () {
        def db = GMongoDBFactory.getDb()
        GridFS gridfs = new GridFS(db);
        gridfs
    }

    static String getFileId(File file, String fileName){
        if (file != null && file.exists()) {
            def gridfs = GridFSUtils.gridfs()
            GridFSInputFile gfsFile = gridfs.createFile(file);
            if (fileName) {
				gfsFile.setFilename(fileName)
				gfsFile.contentType = MimeTypesUtil.getContentType(fileName)
			};
            gfsFile.save()
            return gfsFile._id.toString()
        }
        return null
    }

    static String getFileId(InputStream inputStream, String fileName){
        def gridfs = GridFSUtils.gridfs()
        GridFSInputFile gfsFile = gridfs.createFile(inputStream);
        if (fileName) {
			gfsFile.setFilename(fileName)
			gfsFile.contentType = MimeTypesUtil.getContentType(fileName)
		};
        gfsFile.save()
        return gfsFile._id.toString()
    }

    static GridFSDBFile file(String binaryId) {
        gridfs().findOne(new ObjectId(binaryId));
    }

}
