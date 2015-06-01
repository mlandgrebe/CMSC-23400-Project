# PIL, hashlib

class FileUploadManager(object):
    UPLOAD_DIR = os.path.join(".", "graphs")
    MAX_SIZE_MB = 2
    IMG_FMT = "{}.jpg"

    def __init__(self, upload_dir=UPLOAD_DIR, max_size_mb=MAX_SIZE_MB):
        self.upload_dir = upload_dir
        self._max_size_mb = max_size_mb

    def _build_path(self, img_name, upload_dir=None):
        upload_dir = upload_dir or self.upload_dir
        return self.IMG_FMT.format(os.path.join(upload_dir, img_name))

    def build_filename(self, hashed_image):
        return self.IMG_FMT.format(hashed_image)

    def resize_to_max(self, image):
        raise NotImplementedError

    def build_temp_file(self, image):
        hashed_image = hashlib.md5(image.tostring()).hexdigest()
        image.save(self._build_path(hashed_image))

        return hashed_image

    def image_exists(self, identifier):
        path = self._build_path(identifier)

        ret = os.path.isfile(path)

        return ret

    def path_from_hash_for_send_file(self, hashed_image):
        upload_dir = os.path.join("..", self.upload_dir)
        return self._build_path(hashed_image, upload_dir=upload_dir)
