const profiles = require('./profiles/profiles.service.js');
// eslint-disable-next-line no-unused-vars
module.exports = function (app) {
  app.configure(profiles);
};
