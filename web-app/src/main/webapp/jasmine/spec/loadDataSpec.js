import { filterReports } from "../../script/loadData.js";

describe("Filtering timeframe ", () => {
  const sampleData = [
    {
      yearMonth: "2019-12",
      longitude: "-0.111497",
      latitude: "51.518226",
      crimeType: "Other theft",
    },
  ];

  const filteredReports = filterReports(sampleData, [], 3);
  it("should return true if within timeframe", () => {
    expect(filteredReports.length).toEqual(0);
  });
});
