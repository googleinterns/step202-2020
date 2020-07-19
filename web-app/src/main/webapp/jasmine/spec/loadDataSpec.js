import { filterReports } from "../../script/loadData.js";

describe("Filtering ", () => {
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
  it("should correctly filter out too old data", () => {
    expect(filteredOldReports.length).toEqual(0);
  });
});
