module.exports = (path, options) => {
  return options.defaultResolver(path, {
    ...options,
    packageFilter: (pkg) => {
      if (pkg.name === 'react' || pkg.name === 'react-dom') {
        pkg.main = pkg.exports['.'].node.require;
      }
      return pkg;
    },
  });
}; 