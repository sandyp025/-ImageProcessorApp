
# 📸 ImageProcessorApp

*A lightweight JavaFX demo that applies tile-based image filters synchronously or asynchronously.*

![Java](https://img.shields.io/badge/Java-21-blue?logo=openjdk)
![Build](https://img.shields.io/badge/Maven-Build-success-brightgreen?logo=apachemaven)
![Platform](https://img.shields.io/badge/Platform-macOS%20%7C%20Linux%20%7C%20Windows-lightgrey?logo=apple)
![License](https://img.shields.io/badge/License-MIT-yellow?logo=open-source-initiative)

---

## 🚀 Requirements

* **JDK 21** (make sure `JAVA_HOME` is set)
* **macOS** (commands below are for macOS, but Linux/Windows should also work)
* **Maven wrapper** included (`./mvnw`)
* **Recommended IDE**: IntelliJ IDEA (with JavaFX support) or VS Code

---

## ⚡ Quick Setup (macOS)

```bash
# build
./mvnw clean package

# run
./mvnw javafx:run
```

👉 Or use your IDE:

* Import as a **Maven project**
* Run the main class: `com.my.app.HelloApplication`

---

## 🎯 What it does

* Loads an image from `src/main/resources`.
* Presents a **console menu** + **JavaFX window**.
* Options available:

    1. **Async tile-based processing** (parallel).
    2. **Sync whole-image processing**.
    3. **Exit**.
* Shows **progressive drawing** on the canvas.
* Saves processed images to:

  ```
  output/filtered_<name>.png
  ```

---

## ⚠️ Important Notes

* **Tile size** must evenly divide both image width and height → otherwise processing aborts.
* Async mode uses a **fixed thread pool** (configured in `ImageProcessor`).
* Filters implement `com.my.app.filters.ImageFilter` (see `GreyScaleFilter` as an example).

---

## 🛠️ Extending

* Add new images → `src/main/resources` → update the path in `HelloApplication`.
* Implement new filters → `com.my.app.filters` → wire them in `HelloApplication`.

---

## 🐞 Troubleshooting

* **JavaFX module errors** → Ensure JDK 21 + correct run configuration (use *module path*).
* **Missing images** → Double-check filename + location in `src/main/resources`.

---

## 🧪 Quick Test Plan — Sync vs Async

### Prepare images

* Small: \~800×600
* Medium: \~1920×1080
* Large: \~3000–4000 px

### Steps

1. Run synchronous (`Option 2`) → measure wall time + UI responsiveness.
2. Run asynchronous (`Option 1`) → use tile size that divides dimensions evenly → measure wall time, CPU usage, UI responsiveness.
3. Repeat each run 3–5 times → take **median time**.

### Record

* Elapsed time
* Peak CPU utilization
* Memory usage
* UI responsiveness
* Output image correctness

---

## 📊 Expected Observations

* **Synchronous** → single-threaded, longer wall time, UI may block.
* **Asynchronous** → lower wall time on multi-core CPUs, higher CPU utilization, responsive UI (progressive drawing), but possible overhead if tile size is too small or thread pool too large.
* **Final images** → should be identical (for deterministic filters). Watch for seams/artifacts only if filters need neighboring tile data.

---

## 💡 Quick Tips

* Match **thread pool size** to CPU cores for best performance.
* Use **larger tiles** to reduce scheduling overhead.
* Use **smaller tiles** to increase parallelism (with some overhead).

---

## 📜 License & Author

Made in India ❤️ by **Sandip Mandal** ✨

