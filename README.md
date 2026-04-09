# 2025/2026: 5. Streaming Data Prep in EPL - Bike Sharing and Dutch Energy Generation

Optional project of the [Streaming Data Analytics](https://emanueledellavalle.org/teaching/streaming-data-analytics-2025-26/) course provided by [Politecnico di Milano](https://www11.ceda.polimi.it/schedaincarico/schedaincarico/controller/scheda_pubblica/SchedaPublic.do?&evn_default=evento&c_classe=837284&__pj0=0&__pj1=36cd41e96fcd065c47b49d18e46e3110).

Student: Andrea Lancini

---

# Brief Description

This project focuses on using the Java version of [Esper](https://esper.espertech.com/release-9.0.0/reference-esperio/html_single/index.html#adapter_file) to process a data stream from a CSV file via EPL (Event Processing Language) queries. The primary dataset is the well-known [Bike Sharing Dataset](https://archive.ics.uci.edu/dataset/275/bike+sharing+dataset) from Washington (UCI). The goal is to implement a full streaming feature engineering pipeline ,including temporal transformations, lag features, rolling averages, and exponential moving averages, and then validate the pipeline's generalisability on a second, independent dataset (e.g., the [Dutch Energy Generation dataset](https://www.kaggle.com/datasets/maxscheijen/dutch-energy-generation)).

---

# Project Goals

1. **Setup Esper:** Download the Java version of Esper and learn how to use it to process a data stream from a CSV file.

2. **Write EPL queries** to perform the following transformations on the Bike Sharing stream:
   - Remove the `instant` column;
   - Transform `dteday` into the derived temporal columns: `yr`, `season`, `mnth`, `hr`, `holiday`, `weekday`, `workingday`;
   - Compute `atemp` from `temp` and `hum` using the [apparent temperature formula](https://en.wikipedia.org/wiki/Apparent_temperature)  (the original `atemp` column in the dataset can be used to verify correctness);
   - Compute `cnt` as `casual + registered` ( the original `cnt` column can be used to verify correctness);
   - Add **lag features** for `casual`, `registered`, and `cnt` at lags -1, -24, and -168;
   - Add **rolling mean** features over the last 3, 6, 12, and 24 observations;
   - Add the **Exponential Moving Average (EMA)** of the lag -168 of `casual`, `registered`, and `cnt`.

3. **Validate on a second dataset:** Prove that the pipeline generalises by running it on the [Dutch Energy Generation](https://www.kaggle.com/datasets/maxscheijen/dutch-energy-generation) dataset.

---

# Deliverables

1. **Codebase:** A reproducible Java/Esper repository containing all EPL queries and the ingestion pipeline, released under the **Apache 2.0** licence.
2. **Report / Notebook:** A document presenting the implemented queries, showing that the computed columns match the ground-truth values in the original CSV, and demonstrating that the pipeline works on the Dutch Energy Generation dataset.

---

## Note for Students

* Clone the created repository offline;
* Add your name and surname into this Readme file;
* Add a `requirements.txt` (or equivalent) file for code reproducibility, along with clear instructions on how to replicate the results;
* Commit your changes to your local repository and push them online.
