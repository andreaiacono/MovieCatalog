

This is an android app I wrote for my own home theater system, which is composed by a NAS (accessible via SMB protocol) and a DuneHD player. 

### ![App Screenshot](<https://raw.githubusercontent.com/andreaiacono/MovieCatalog/master/screenshot.png>)



### Usage

At the first run, tap the NAS download icon to download the movies data from the directory containing the movies. Then you can filter them by genre or sort them. Tapping on a thumbnail, you can see the full movie description and on the menu you can look for a trailer of the movie on youtube or launching the movie on the Dune HD player.

### Configuration

Copy the [/app/src/main/res/raw/default_config.yaml](https://github.com/andreaiacono/MovieCatalog/tree/master/app/src/main/res/rawapp/src/main/res/default_config.yaml) to a new file named `config.yaml` in the same directory and change the values accordingly to your configuration; the app will read the config from that file.

The configuration contains two properties:

```yaml
nasUrl: smb://192.168.1.2/shared/public/
duneIp: 192.168.1.3
```

### Data

The `nasUrl` property contains a URL to the directory containing the movies; every movie must be inside a directory containing 4 files:

* the configuration XML file
* a thumbnail and a full screen image
* the movie itself

 For example, the movie _Arrival_ will be inside a directory called `Arrival` containing these files: 

* `arrival.xml`
* `about.jpg`
* `fodler.jpg`
* `arrival.mkv`

I use Yadis to generate the images and Yadis2mede8er for generating the XML file.

### Build

On Android Studio, you can generate the APK using the menu Build -> Generate Signed Bundle / APK or see the related [article](https://developer.android.com/studio/run).

