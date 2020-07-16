import { testFunc } from "../../script/loadData.js";

// describe("LoadData", function () {
//   var LoadData = require('../../script/loadData');

//   describe("when spy is created", function () {
//     var jsdom = require("jsdom");
//     const { JSDOM } = jsdom;

//     const { document } = (new JSDOM('<html><head></head><body><div id="rondavu_container"></div></body></html>')).window;

//     var dummyDiv = document.createElement('div');
//     document.getElementById = jasmine.createSpy('HTML Div').and.returnValue(dummyDiv);

//     it("should work", function () {
//       const div = document.getElementById('div');
//       expect(div.nodeName).toEqual('DIV');
//     })
//   })
// })

// import { testFunc } from "../../script/loadData.js";
// describe("Filtering timeframe ", () => {
//   const sampleData = [
//     {
//       yearMonth: "2019-12",
//       longitude: "-0.111497",
//       latitude: "51.518226",
//       crimeType: "Other theft",
//     },
//   ];

//   const filteredReports = filterReports(sampleData, [], 3);
//   it("should return true if within timeframe", () => {
//     expect(filteredReports.length).toEqual(0);
//   });
// });

describe("My test", function () {
  it("runs", function () {
    const result = testFunc();
    expect(result).toBe(true);
  });
});
