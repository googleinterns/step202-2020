import { filterReports } from "../../script/loadData.js";

describe("Filtering ", () => {
  it("should correctly filter out too old data", () => {
    const sampleDataOld = [
      {
        yearMonth: "2019-12",
        longitude: "-0.111497",
        latitude: "51.518226",
        crimeType: "Other theft",
      },
      {
        yearMonth: "2019-12",
        longitude: "-0.111497",
        latitude: "51.518226",
        crimeType: "Theft from the person",
      },
      {
        yearMonth: "2019-12",
        longitude: "-0.097562",
        latitude: "51.518864",
        crimeType: "Anti-social behaviour",
      },
    ];

    const filteredOldReports = filterReports(sampleDataOld, [], 3);
    expect(filteredOldReports.length).toEqual(0);
  });

  it("should not filter out new data", () => {
    const today = new Date();
    // Two months before current date
    const sampleYearMonth = `${today.getFullYear()}-${today.getMonth() - 1}`;

    const sampleDataNew = [
      {
        yearMonth: sampleYearMonth,
        longitude: "-0.111497",
        latitude: "51.518226",
        crimeType: "Other theft",
      },
      {
        yearMonth: sampleYearMonth,
        longitude: "-0.111497",
        latitude: "51.518226",
        crimeType: "Theft from the person",
      },
      {
        yearMonth: sampleYearMonth,
        longitude: "-0.097562",
        latitude: "51.518864",
        crimeType: "Anti-social behaviour",
      },
    ];
    
    const filteredNewReports = filterReports(sampleDataNew, [], 3);
    expect(filteredNewReports.length).toEqual(3);
  });

  it("should filter out reports with unchecked categories", () => {
    const today = new Date();
    // Two months before current date
    const sampleYearMonth = `${today.getFullYear()}-${today.getMonth() - 1}`;

    const sampleDataNew = [
      {
        yearMonth: sampleYearMonth,
        longitude: "-0.111497",
        latitude: "51.518226",
        crimeType: "Other theft",
      },
      {
        yearMonth: sampleYearMonth,
        longitude: "-0.111497",
        latitude: "51.518226",
        crimeType: "Theft from the person",
      },
      {
        yearMonth: sampleYearMonth,
        longitude: "-0.097562",
        latitude: "51.518864",
        crimeType: "Anti-social behaviour",
      },
    ];

    const categoriesFiltered = filterReports(sampleDataNew, ["theft"], 3);
    expect(categoriesFiltered.length).toEqual(1);
  });
});
