  private void FileDownload(String downloadUrl, String FileName) {
        String SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath(); // location where you want to store
        File directory = new File(SDCardRoot, "/download_folder/"); //create directory to keep your downloaded file
        if (!directory.exists()) {
            directory.mkdir();
        }
//            String fileName = "mySong" + ".mp3"; //song name that will be stored in your device in case of song
        //String fileName = "myImage" + ".jpeg"; in case of image
        try {
            InputStream input = null;
            try {

                URL url = new URL(downloadUrl.toString()); // link of the song which you want to download like (http://...)
                input = url.openStream();
                OutputStream output = new FileOutputStream(new File(directory, FileName));
                try {
                    byte[] buffer = new byte[1024];
                    int bytesRead = 0;
                    while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                        output.write(buffer, 0, bytesRead);
                    }
                    output.close();
                } catch (Exception exception) {
                    exception.printStackTrace();
//                        Sys"output exception in catch....." + exception + "");
                    output.close();
//                        progressDialog.dismiss();
                }
            } catch (Exception exception) {
//                    MyUtility.showLog("input exception in catch....." + exception + "");
                exception.printStackTrace();
//                    progressDialog.dismiss();
            } finally {
                input.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
//                progressDialog.dismiss();
        }
    }
