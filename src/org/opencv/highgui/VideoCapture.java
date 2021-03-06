package org.opencv.highgui;

import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.util.LinkedList;
import java.util.List;

// C++: class VideoCapture

/**
 * Class for video capturing from video files or cameras.
 * The class provides C++ API for capturing video from cameras or for reading
 * video files. Here is how the class can be used:
 *
 * Note: In C API the black-box structure "CvCapture" is used instead of
 * "VideoCapture".
 *
 * @see <a href="http://opencv.itseez.com/modules/highgui/doc/reading_and_writing_images_and_video.html#videocapture">org.opencv.highgui.VideoCapture</a>
 */
public class VideoCapture {

    protected final long nativeObj;

    protected VideoCapture(long addr) {
        nativeObj = addr;
    }

    //
    // C++: VideoCapture::VideoCapture()
    //

/**
 * VideoCapture constructors.
 *
 * Note: In C API, when you finished working with video, release "CvCapture"
 * structure with "cvReleaseCapture()", or use "Ptr<CvCapture>" that calls
 * "cvReleaseCapture()" automatically in the destructor.
 *
 * @see <a href="http://opencv.itseez.com/modules/highgui/doc/reading_and_writing_images_and_video.html#videocapture-videocapture">org.opencv.highgui.VideoCapture.VideoCapture</a>
 */
    public VideoCapture()
    {

        nativeObj = n_VideoCapture();

        return;
    }

    //
    // C++: VideoCapture::VideoCapture(int device)
    //

/**
 * VideoCapture constructors.
 *
 * Note: In C API, when you finished working with video, release "CvCapture"
 * structure with "cvReleaseCapture()", or use "Ptr<CvCapture>" that calls
 * "cvReleaseCapture()" automatically in the destructor.
 *
 * @param device id of the opened video capturing device (i.e. a camera index).
 * If there is a single camera connected, just pass 0.
 *
 * @see <a href="http://opencv.itseez.com/modules/highgui/doc/reading_and_writing_images_and_video.html#videocapture-videocapture">org.opencv.highgui.VideoCapture.VideoCapture</a>
 */
    public VideoCapture(int device)
    {

        nativeObj = n_VideoCapture(device);

        return;
    }

    //
    // C++: double VideoCapture::get(int propId)
    //

/**
 * Returns the specified "VideoCapture" property
 *
 * Note: When querying a property that is not supported by the backend used by
 * the "VideoCapture" class, value 0 is returned.
 *
 * @param propId Property identifier. It can be one of the following:
 *   * CV_CAP_PROP_FRAME_WIDTH Width of the frames in the video stream.
 *   * CV_CAP_PROP_FRAME_HEIGHT Height of the frames in the video stream.
 *
 * @see <a href="http://opencv.itseez.com/modules/highgui/doc/reading_and_writing_images_and_video.html#videocapture-get">org.opencv.highgui.VideoCapture.get</a>
 */
    public double get(int propId)
    {

        double retVal = n_get(nativeObj, propId);

        return retVal;
    }

    public List<Size> getSupportedPreviewSizes()
    {
        String[] sizes_str = n_getSupportedPreviewSizes(nativeObj).split(",");
        List<Size> sizes = new LinkedList<Size>();

        for (String str : sizes_str) {
            String[] wh = str.split("x");
            sizes.add(new Size(Double.parseDouble(wh[0]), Double.parseDouble(wh[1])));
        }

        return sizes;
    }

    //
    // C++: bool VideoCapture::grab()
    //

/**
 * Grabs the next frame from video file or capturing device.
 *
 * The methods/functions grab the next frame from video file or camera and
 * return true (non-zero) in the case of success.
 *
 * The primary use of the function is in multi-camera environments, especially
 * when the cameras do not have hardware synchronization. That is, you call
 * "VideoCapture.grab()" for each camera and after that call the slower method
 * "VideoCapture.retrieve()" to decode and get frame from each camera. This way
 * the overhead on demosaicing or motion jpeg decompression etc. is eliminated
 * and the retrieved frames from different cameras will be closer in time.
 *
 * Also, when a connected camera is multi-head (for example, a stereo camera or
 * a Kinect device), the correct way of retrieving data from it is to call
 * "VideoCapture.grab" first and then call "VideoCapture.retrieve" one or more
 * times with different values of the "channel" parameter. See https://code.ros.org/svn/opencv/trunk/opencv/samples/cpp/kinect_maps.cpp
 *
 * @see <a href="http://opencv.itseez.com/modules/highgui/doc/reading_and_writing_images_and_video.html#videocapture-grab">org.opencv.highgui.VideoCapture.grab</a>
 */
    public boolean grab()
    {

        boolean retVal = n_grab(nativeObj);

        return retVal;
    }

    //
    // C++: bool VideoCapture::isOpened()
    //

/**
 * Returns true if video capturing has been initialized already.
 *
 * If the previous call to "VideoCapture" constructor or "VideoCapture.open"
 * succeeded, the method returns true.
 *
 * @see <a href="http://opencv.itseez.com/modules/highgui/doc/reading_and_writing_images_and_video.html#videocapture-isopened">org.opencv.highgui.VideoCapture.isOpened</a>
 */
    public boolean isOpened()
    {

        boolean retVal = n_isOpened(nativeObj);

        return retVal;
    }

    //
    // C++: bool VideoCapture::open(int device)
    //

/**
 * Open video file or a capturing device for video capturing
 *
 * The methods first call "VideoCapture.release" to close the already opened
 * file or camera.
 *
 * @param device id of the opened video capturing device (i.e. a camera index).
 *
 * @see <a href="http://opencv.itseez.com/modules/highgui/doc/reading_and_writing_images_and_video.html#videocapture-open">org.opencv.highgui.VideoCapture.open</a>
 */
    public boolean open(int device)
    {

        boolean retVal = n_open(nativeObj, device);

        return retVal;
    }

    //
    // C++: bool VideoCapture::read(Mat image)
    //

/**
 * Grabs, decodes and returns the next video frame.
 *
 * The methods/functions combine "VideoCapture.grab" and "VideoCapture.retrieve"
 * in one call. This is the most convenient method for reading video files or
 * capturing data from decode and retruen the just grabbed frame. If no frames
 * has been grabbed (camera has been disconnected, or there are no more frames
 * in video file), the methods return false and the functions return NULL
 * pointer.
 *
 * Note: OpenCV 1.x functions "cvRetrieveFrame" and "cv.RetrieveFrame" return
 * image stored inside the video capturing structure. It is not allowed to
 * modify or release the image! You can copy the frame using "cvCloneImage" and
 * then do whatever you want with the copy.
 *
 * @param image a image
 *
 * @see <a href="http://opencv.itseez.com/modules/highgui/doc/reading_and_writing_images_and_video.html#videocapture-read">org.opencv.highgui.VideoCapture.read</a>
 */
    public boolean read(Mat image)
    {

        boolean retVal = n_read(nativeObj, image.nativeObj);

        return retVal;
    }

    //
    // C++: void VideoCapture::release()
    //

/**
 * Closes video file or capturing device.
 *
 * The methods are automatically called by subsequent "VideoCapture.open" and
 * by "VideoCapture" destructor.
 *
 * The C function also deallocates memory and clears "*capture" pointer.
 *
 * @see <a href="http://opencv.itseez.com/modules/highgui/doc/reading_and_writing_images_and_video.html#videocapture-release">org.opencv.highgui.VideoCapture.release</a>
 */
    public void release()
    {

        n_release(nativeObj);

        return;
    }

    //
    // C++: bool VideoCapture::retrieve(Mat image, int channel = 0)
    //

/**
 * Decodes and returns the grabbed video frame.
 *
 * The methods/functions decode and retruen the just grabbed frame. If no frames
 * has been grabbed (camera has been disconnected, or there are no more frames
 * in video file), the methods return false and the functions return NULL
 * pointer.
 *
 * Note: OpenCV 1.x functions "cvRetrieveFrame" and "cv.RetrieveFrame" return
 * image stored inside the video capturing structure. It is not allowed to
 * modify or release the image! You can copy the frame using "cvCloneImage" and
 * then do whatever you want with the copy.
 *
 * @param image a image
 * @param channel a channel
 *
 * @see <a href="http://opencv.itseez.com/modules/highgui/doc/reading_and_writing_images_and_video.html#videocapture-retrieve">org.opencv.highgui.VideoCapture.retrieve</a>
 */
    public boolean retrieve(Mat image, int channel)
    {

        boolean retVal = n_retrieve(nativeObj, image.nativeObj, channel);

        return retVal;
    }

/**
 * Decodes and returns the grabbed video frame.
 *
 * The methods/functions decode and retruen the just grabbed frame. If no frames
 * has been grabbed (camera has been disconnected, or there are no more frames
 * in video file), the methods return false and the functions return NULL
 * pointer.
 *
 * Note: OpenCV 1.x functions "cvRetrieveFrame" and "cv.RetrieveFrame" return
 * image stored inside the video capturing structure. It is not allowed to
 * modify or release the image! You can copy the frame using "cvCloneImage" and
 * then do whatever you want with the copy.
 *
 * @param image a image
 *
 * @see <a href="http://opencv.itseez.com/modules/highgui/doc/reading_and_writing_images_and_video.html#videocapture-retrieve">org.opencv.highgui.VideoCapture.retrieve</a>
 */
    public boolean retrieve(Mat image)
    {

        boolean retVal = n_retrieve(nativeObj, image.nativeObj);

        return retVal;
    }

    //
    // C++: bool VideoCapture::set(int propId, double value)
    //

/**
 * Sets a property in the "VideoCapture".
 *
 * @param propId Property identifier. It can be one of the following:
 *   * CV_CAP_PROP_FRAME_WIDTH Width of the frames in the video stream.
 *   * CV_CAP_PROP_FRAME_HEIGHT Height of the frames in the video stream.
 * @param value Value of the property.
 *
 * @see <a href="http://opencv.itseez.com/modules/highgui/doc/reading_and_writing_images_and_video.html#videocapture-set">org.opencv.highgui.VideoCapture.set</a>
 */
    public boolean set(int propId, double value)
    {

        boolean retVal = n_set(nativeObj, propId, value);

        return retVal;
    }

    @Override
    protected void finalize() throws Throwable {
        n_delete(nativeObj);
        super.finalize();
    }

    // native stuff

    static {
        System.loadLibrary("opencv_java");
    }

    // C++: VideoCapture::VideoCapture()
    private static native long n_VideoCapture();

    // C++: VideoCapture::VideoCapture(string filename)
    private static native long n_VideoCapture(String filename);

    // C++: VideoCapture::VideoCapture(int device)
    private static native long n_VideoCapture(int device);

    // C++: double VideoCapture::get(int propId)
    private static native double n_get(long nativeObj, int propId);

    // C++: bool VideoCapture::grab()
    private static native boolean n_grab(long nativeObj);

    // C++: bool VideoCapture::isOpened()
    private static native boolean n_isOpened(long nativeObj);

    // C++: bool VideoCapture::open(string filename)
    private static native boolean n_open(long nativeObj, String filename);

    // C++: bool VideoCapture::open(int device)
    private static native boolean n_open(long nativeObj, int device);

    // C++: bool VideoCapture::read(Mat image)
    private static native boolean n_read(long nativeObj, long image_nativeObj);

    // C++: void VideoCapture::release()
    private static native void n_release(long nativeObj);

    // C++: bool VideoCapture::retrieve(Mat image, int channel = 0)
    private static native boolean n_retrieve(long nativeObj, long image_nativeObj, int channel);

    private static native boolean n_retrieve(long nativeObj, long image_nativeObj);

    // C++: bool VideoCapture::set(int propId, double value)
    private static native boolean n_set(long nativeObj, int propId, double value);

    private static native String n_getSupportedPreviewSizes(long nativeObj);

    // native support for java finalize()
    private static native void n_delete(long nativeObj);

}
