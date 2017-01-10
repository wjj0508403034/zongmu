var gulp = require('gulp');
var del = require('del');
var showFile = require('gulp-show-me-file');
var wiredep = require('wiredep').stream;
var concat = require('gulp-concat');
var minifyHtml = require('gulp-minify-html');
var angularTemplatecache = require('gulp-angular-templatecache');
var es = require('event-stream');
var inject = require('gulp-inject');

gulp.task('default', ['build-app']);

gulp.task('clean', function() {
  return del(['dist']);
});

gulp.task('huoyun-css', ['clean'], function() {
  return gulp.src('src/common/**/*.css')
    .pipe(showFile())
    .pipe(concat("huoyun.ui.css"))
    .pipe(gulp.dest('dist/libs'))
    .pipe(gulp.dest('../zongmu-service/src/main/webapp/resources/libs'));
});

gulp.task('huoyun-js', ['clean'], function() {
  var templateStream = gulp.src('src/common/**/*.html')
    .pipe(showFile())
    .pipe(minifyHtml({
      empty: true,
      spare: true,
      quotes: true
    }))
    .pipe(angularTemplatecache('huoyun.ui.tpl.js', {
      module: 'huoyun-ui'
    }));

  var es = require('event-stream');
  return es.merge([
      templateStream,
      gulp.src('src/common/**/*.js')
    ])
    .pipe(showFile())
    .pipe(concat('huoyun.ui.js'))
    // .pipe(uglify())
    .pipe(gulp.dest('dist/libs'))
    .pipe(gulp.dest('../zongmu-service/src/main/webapp/resources/libs'));
});

gulp.task('build-huoyun', ['huoyun-css', 'huoyun-js']);

gulp.task("thirdparty", ['clean'], function() {
  return gulp.src('libs/**')
    .pipe(showFile())
    .pipe(gulp.dest('dist/libs'))
    .pipe(gulp.dest('../zongmu-service/src/main/webapp/resources/libs'));
});

gulp.task('app-css', ['clean'], function() {
  return gulp.src('src/views/**/*.css')
    .pipe(showFile())
    .pipe(concat("app.css"))
    .pipe(gulp.dest('dist/app'))
    .pipe(gulp.dest('../zongmu-service/src/main/webapp/resources/app'));
});

gulp.task('app-js', ['clean'], function() {
  return gulp.src('src/views/**/*.js')
    .pipe(showFile())
    .pipe(concat("app.js"))
    .pipe(gulp.dest('dist/app'))
    .pipe(gulp.dest('../zongmu-service/src/main/webapp/resources/app'));
});

gulp.task('app-img', ['clean'], function() {
  return gulp.src('img/**')
    .pipe(showFile())
    .pipe(gulp.dest('dist/img'))
    .pipe(gulp.dest('../zongmu-service/src/main/webapp/resources/imgs'));
});

gulp.task('build-app', ['thirdparty', 'app-css', 'app-js', 'build-huoyun'], function() {
  var injectCss = gulp.src([
    'dist/libs/**/*.css',
    'dist/app/**/*.css'
  ], {
    read: false
  });

  var injectJs = gulp.src([
    'dist/libs/jquery-2.1.3.min.js', // 必须把jquery放在第一个文件，后面很多模块依赖jquery
    'dist/libs/jquery-ui/jquery-ui.min.js',
    'dist/libs/angular.js',
    'dist/libs/**/*.js',
    'dist/app/**/*.js'
  ], {
    read: false
  });

  return gulp.src('src/views/**/*.html')
    .pipe(showFile())
    .pipe(inject(injectCss, {
      relative: true
    }))
    .pipe(inject(injectJs, {
      relative: true
    }))
    .pipe(gulp.dest('dist/app'));
});

gulp.task('deploy', ['thirdparty', 'app-img', 'app-css', 'app-js', 'build-huoyun'], function() {
  var injectCss = gulp.src([
    '../zongmu-service/src/main/webapp/resources/libs/**/*.css',
    '../zongmu-service/src/main/webapp/resources/app/**/*.css'
  ], {
    read: false
  });

  var injectJs = gulp.src([
    '../zongmu-service/src/main/webapp/resources/libs/jquery-2.1.3.min.js', // 必须把jquery放在第一个文件，后面很多模块依赖jquery
    '../zongmu-service/src/main/webapp/resources/libs/jquery-ui/jquery-ui.min.js',
    '../zongmu-service/src/main/webapp/resources/libs/angular.js',
    '../zongmu-service/src/main/webapp/resources/libs/js.cookie.js',
    '../zongmu-service/src/main/webapp/resources/libs/**/*.js',
    '../zongmu-service/src/main/webapp/resources/app/**/*.js'
  ], {
    read: false
  });

  return gulp.src('src/views/**/*.html')
    .pipe(showFile())
    .pipe(inject(injectCss, {
      transform: function(filepath) {
        filepath = filepath.replace("../zongmu-service/src/main/webapp/resources", "resources");
        return '<link rel="stylesheet" href="../..' + filepath + '">';
      }
    }))
    .pipe(inject(injectJs, {
      transform: function(filepath) {
        filepath = filepath.replace("../zongmu-service/src/main/webapp/resources", "resources");
        return '<script src="../..' + filepath + '"></script>';
      }
    }))
    .pipe(gulp.dest('../zongmu-service/src/main/webapp/WEB-INF/views'));
});