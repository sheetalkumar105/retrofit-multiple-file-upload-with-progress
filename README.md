# retrofit-multiple-file-upload-with-progress
Upload multiple files with progressbar in retrofit android


![Sample Home](https://raw.githubusercontent.com/sheetalkumar105/retrofit-multiple-file-upload-with-progress/master/sample.gif)


Retrofit does not have any function to upload multiple files files with progress callback. I have created a class  FileUploader that makes multiple files upload easy with retrofit.


    public void uploadFiles(){
        File[] filesToUpload = new File[files.size()];
        for(int i=0; i< files.size(); i++){
            filesToUpload[i] = new File(files.get(i));
        }
        showProgress("Uploading media ...");
        FileUploader fileUploader = new FileUploader();
        fileUploader.uploadFiles("/", "file", filesToUpload, new FileUploader.FileUploaderCallback() {
            @Override
            public void onError() {
                hideProgress();
            }

            @Override
            public void onFinish(String[] responses) {
                hideProgress();
                for(int i=0; i< responses.length; i++){
                    String str = responses[i];
                    Log.e("RESPONSE "+i, responses[i]);
                }
            }

            @Override
            public void onProgressUpdate(int currentpercent, int totalpercent, int filenumber) {
                updateProgress(totalpercent,"Uploading file "+filenumber,"");
                Log.e("Progress Status", currentpercent+" "+totalpercent+" "+filenumber);
            }
        });
    }


Demo is available on DevStudioOnline:
[Retrofit multiple file upload with progress in android](https://devstudioonline.com/article/retrofit-multiple-file-upload-with-progress-in-android)
