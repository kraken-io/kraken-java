# Kraken.io java client

## Introduction
[Kraken.io's](https://kraken.io/) image optimization API provides advanced optimization for your JPEG, PNG, GIF and SVG images.
Established in 2012, Kraken.io is an industry-leading image optimizer like no other capable of significantly reducing the file size of popular image formats using tried-and-tested optimization techniques developed with a single goal in mind: To reduce their file size by as much as possible while retaining the image quality.

Kraken.io's API includes image resizing, in addition to its core optimization functionality, for those who need to resize their images prior to optimization.

**Kraken.io is trusted by thousands of customers worldwide, from individuals to small/medium sized businesses and even Fortune 500 companies.**

## Benefits of image optimization
In nutshell, faster loading sites are viewed more favourably than slower loading sites by both users and search engines. Getting your site to load faster can go a long way to improving your business's bottom line. For more information, visit our [Support Center](https://support.kraken.io/).

Got a question? Need some help? Get in touch with Kraken.io support, which is easily visible when logged in to Kraken.io.


## Kraken.io is more than just an API
For our complete list of API functionality, integration modules, third party plugins, and more, please visit our [official documentation website](https://kraken.io/docs/getting-started).

You can try us out by creating a [free account](https://kraken.io/signup) and/or visiting out our [Free Web Interface](https://kraken.io/web-interface).

## Compatibility
- Java 6+

## Dependencies

### Maven
```xml
<dependency>
    <groupId>io.kraken.client</groupId>
    <artifactId>client</artifactId>
    <version>1.1.0</version>
</dependency>
```

## How to use

### Builders
Name                                | Description
----------------------------------- | --------------------------------------------------
 DirectFileUploadRequest            | Upload based on a File
 DirectUploadRequest                | Upload based on a InputStream
 ImageUrlUploadRequest              | Image url request
 DirectFileUploadCallbackUrlRequest | Upload based on a File with callback url
 DirectUploadCallbackUrlRequest     | Upload based on a InputStream with a callback url
 ImageUrlUploadCallbackUrlRequest   | Image url request with callback url
 
### Resize
Name                | Description
------------------- | --------------------------------------------------------------------------------
 AutoResize         | The best strategy (portrait or landscape) will be selected for a given image according to its aspect ratio
 CropResize         | This option will crop your images to the exact size you specify with no distortion
 ExactResize        | Resize to exact width and height. Aspect ratio will not be maintained
 FillResize         | This strategy allows you to resize the image to fit the specified bounds while preserving the aspect ratio (just like auto strategy). The optional background property allows you to specify a color which will be used to fill the unused portions of the previously specified bounds
 FitResize          | This option will crop and resize your images to fit the desired width and height
 LandscapeResize    | Exact width will be set, height will be adjusted according to aspect ratio
 PortraitResize     | Exact height will be set, width will be adjusted according to aspect ratio
 SquareResize       | This strategy will first crop the image by its shorter dimension to make it a square, then resize it to the specified size

### Direct image upload

```java
  final KrakenIoClient krakenIoClient = new DefaultKrakenIoClient("somekey", "somesecret");

  // InputStream direct file upload
  final FillResize fillResize = new FillResize(150, 150, new RGBA(100, 100, 100, BigDecimal.ONE));
  final DirectUploadRequest directUploadRequest = DirectUploadRequest.builder(
    new ByteArrayInputStream(loadFileBinary("test.jpg"))
  )
  .withResize(fillResize)
  .build();
  
  try {
      final SuccessfulUploadResponse successfulUploadResponse = krakenIoClient.directUpload(directUploadRequest);
  } catch(KrakenIoRequestException e) {
      final FailedUploadResponse failedUploadResponse = e.getFailedUploadResponse();
  }
  
  // File based direct file upload
  final File image = new File("path/to/file.jpg");
  final FillResize fillResize = new FillResize(150, 150, new RGBA(100, 100, 100, BigDecimal.ONE));
  final DirectFileUploadRequest directFileUploadRequest = DirectFileUploadRequest.builder(image)
  .withResize(fillResize)
  .build();
  
  try {
      final SuccessfulUploadResponse successfulUploadResponse = krakenIoClient.directUpload(directFileUploadRequest);
  } catch(KrakenIoRequestException e) {
      final FailedUploadResponse failedUploadResponse = e.getFailedUploadResponse();
  }
  
  // InputStream direct upload with callback url
  final FillResize fillResize = new FillResize(150, 150, new RGBA(100, 100, 100, BigDecimal.ONE));
  final DirectUploadCallbackUrlRequest directUploadCallbackUrlRequest = DirectUploadCallbackUrlRequest.builder(
    new ByteArrayInputStream(loadFileBinary("test.jpg")), 
    new URL("http://somehost/somecallback")
  )
  .withResize(fillResize)
  .build();
  
  try {
      final SuccessfulUploadCallbackUrlResponse successfulUploadCallbackUrlResponse = krakenIoClient.directUpload(directUploadCallbackUrlRequest);
  } catch(KrakenIoRequestException e) {
      final FailedUploadResponse failedUploadResponse = e.getFailedUploadResponse();
  }
  
  // File based direct upload with callback url
  final File image = new File("path/to/file.jpg");
  final FillResize fillResize = new FillResize(150, 150, new RGBA(100, 100, 100, BigDecimal.ONE));
  final DirectFileUploadCallbackUrlRequest directFileUploadCallbackUrlRequest = DirectFileUploadCallbackUrlRequest.builder(
    image, 
    new URL("http://somehost/somecallback")
  )
  .withResize(fillResize)
  .build();
  
  try {
      final SuccessfulUploadCallbackUrlResponse successfulUploadCallbackUrlResponse = krakenIoClient.directUpload(directFileUploadCallbackUrlRequest);
  } catch(KrakenIoRequestException e) {
      final FailedUploadResponse failedUploadResponse = e.getFailedUploadResponse();
  }
```

### Image url
```java
  // Image url request
  final FillResize fillResize = new FillResize(150, 150, new RGBA(100, 100, 100, BigDecimal.ONE));
  final ImageUrlUploadRequest imageUrlUploadRequest = ImageUrlUploadRequest.builder(
    new URL("http://somehost/image")
  )
  .withResize(fillResize)
  .build();
  
  try {
      final SuccessfulUploadResponse successfulUploadResponse = krakenIoClient.imageUrlUpload(imageUrlUploadRequest);
  } catch(KrakenIoRequestException e) {
      final FailedUploadResponse failedUploadResponse = e.getFailedUploadResponse();
  }
  
  // Image url with callback url request
  final FillResize fillResize = new FillResize(150, 150, new RGBA(100, 100, 100, BigDecimal.ONE));
  final ImageUrlUploadCallbackUrlRequest imageUrlUploadCallbackUrlRequest = ImageUrlUploadCallbackUrlRequest
                .builder(new URL("http://somehost/image"), new URL("http://somehost/somecallback"))
                .withResize(fillResize)
                .build();
  try {
      final SuccessfulUploadCallbackUrlResponse successfulUploadCallbackUrlResponse = krakenIoClient.imageUrlUpload(imageUrlUploadCallbackUrlRequest);
  } catch(KrakenIoRequestException e) {
      final FailedUploadResponse failedUploadResponse = e.getFailedUploadResponse();
  }
```

### Access the API Sandbox
```java
  final FillResize fillResize = new FillResize(150, 150, new RGBA(100, 100, 100, BigDecimal.ONE));
  final DirectUploadRequest directUploadRequest = DirectUploadRequest.builder(
    new ByteArrayInputStream(loadFileBinary("test.jpg"))
  )
  .withDev(true) // Sandbox mode
  .build();

  try {
      final SuccessfulUploadResponse successfulUploadResponse = krakenIoClient.directUpload(directUploadRequest);
  } catch(KrakenIoRequestException e) {
      final FailedUploadResponse failedUploadResponse = e.getFailedUploadResponse();
  }
```

Contribution
------------

Any contributions are highly appreciated. The best way to contribute code is to open a
[pull request on GitHub](https://help.github.com/articles/using-pull-requests).

## License
Apache License, Version 2.0
