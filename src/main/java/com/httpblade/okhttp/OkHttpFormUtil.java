package com.httpblade.okhttp;

import com.httpblade.common.ContentType;
import com.httpblade.common.form.Field;
import com.httpblade.common.form.FileField;
import com.httpblade.common.form.Form;
import com.httpblade.common.form.StreamField;
import okhttp3.*;

import java.util.List;

class OkHttpFormUtil {

    static void createGetUrl(Form form, HttpUrl.Builder urlBuilder) {
        List<Field> fields = form.fields();
        if (fields.size() > 0) {
            for (Field field : fields) {
                if (field.encoded()) {
                    urlBuilder.addEncodedQueryParameter(field.name(), field.value());
                } else {
                    urlBuilder.addQueryParameter(field.name(), field.value());
                }
            }
        }
    }

    static RequestBody createRequestBody(Form form) {
        List<FileField> fileFields = form.fileFields();
        List<StreamField> streamFields = form.streamFields();
        List<Field> fields = form.fields();
        if (fileFields.size() > 0 || streamFields.size() > 0) {
            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder(form.getBoundary());
            for (Field field : fields) {
                multipartBuilder.addFormDataPart(field.name(), field.value());
            }
            for (FileField fileField : fileFields) {
                multipartBuilder.addFormDataPart(fileField.name(), fileField.fileName(),
                    RequestBody.create(MediaType.parse(ContentType.OCTET_STREAM), fileField.file()));
            }
            for (StreamField streamField : streamFields) {
                multipartBuilder.addFormDataPart(streamField.name(), streamField.fileName(),
                    new StreamRequestBody(streamField.inputStream()));
            }
            return multipartBuilder.build();
        } else {
            FormBody.Builder formBuilder = new FormBody.Builder();
            for (Field field : fields) {
                if (field.encoded()) {
                    formBuilder.addEncoded(field.name(), field.value());
                } else {
                    formBuilder.add(field.name(), field.value());
                }
            }
            return formBuilder.build();
        }
    }

}
