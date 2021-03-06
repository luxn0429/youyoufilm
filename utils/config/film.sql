/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50520
Source Host           : localhost:3306
Source Database       : film

Target Server Type    : MYSQL
Target Server Version : 50520
File Encoding         : 65001

Date: 2013-02-20 16:54:10
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `language`
-- ----------------------------
DROP TABLE IF EXISTS `language`;
CREATE TABLE `language` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of language
-- ----------------------------
INSERT INTO language VALUES ('1', '国语');
INSERT INTO language VALUES ('2', '粤语');
INSERT INTO language VALUES ('3', '英语');
INSERT INTO language VALUES ('4', '韩语');
INSERT INTO language VALUES ('5', '日语');
INSERT INTO language VALUES ('6', '泰语');

-- ----------------------------
-- Table structure for `monthvideolist`
-- ----------------------------
DROP TABLE IF EXISTS `monthvideolist`;
CREATE TABLE `monthvideolist` (
  `month` int(10) unsigned NOT NULL COMMENT '月榜单是哪个月',
  `type` int(10) unsigned NOT NULL COMMENT '榜单类型',
  `content` blob NOT NULL COMMENT '榜单内容，大对象存储',
  UNIQUE KEY `NewIndex2` (`month`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of monthvideolist
-- ----------------------------

-- ----------------------------
-- Table structure for `video`
-- ----------------------------
DROP TABLE IF EXISTS `video`;
CREATE TABLE `video` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '电影或电视剧ID',
  `name` varchar(32) NOT NULL COMMENT '名字',
  `type` int(10) DEFAULT NULL COMMENT '电视剧电影类型：喜剧、恐怖等可以是多种类型',
  `country` int(10) unsigned NOT NULL COMMENT '国别',
  `pubdate` int(10) unsigned NOT NULL COMMENT '出品日期',
  `caption` int(11) unsigned DEFAULT NULL COMMENT '字幕',
  `language` int(11) unsigned DEFAULT NULL COMMENT '语言',
  `updateTime` int(10) unsigned DEFAULT NULL COMMENT '入库时间',
  `performer` varchar(1024) DEFAULT NULL COMMENT '主演',
  `poster` varchar(1024) DEFAULT NULL COMMENT '海报',
  `introduction` text COMMENT '简介',
  `state` tinyint(3) unsigned NOT NULL COMMENT '更新状态',
  `format` int(11) DEFAULT NULL COMMENT '格式：DVD',
  `clarity` varchar(32) DEFAULT NULL COMMENT '清晰度',
  `classified` tinyint(4) DEFAULT NULL COMMENT '分类  1. 电影  2电视剧 3综艺娱乐',
  `seriousIntro` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `NewIndex5` (`name`),
  KEY `NewIndex1` (`type`),
  KEY `NewIndex2` (`country`),
  KEY `NewIndex3` (`pubdate`),
  KEY `NewIndex4` (`language`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of video
-- ----------------------------

-- ----------------------------
-- Table structure for `videocountry`
-- ----------------------------
DROP TABLE IF EXISTS `videocountry`;
CREATE TABLE `videocountry` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of videocountry
-- ----------------------------
INSERT INTO videocountry VALUES ('1', '大陆');
INSERT INTO videocountry VALUES ('2', '香港');
INSERT INTO videocountry VALUES ('3', '台湾');
INSERT INTO videocountry VALUES ('4', '韩国');
INSERT INTO videocountry VALUES ('5', '美国');
INSERT INTO videocountry VALUES ('6', '日本');
INSERT INTO videocountry VALUES ('7', '泰国');
INSERT INTO videocountry VALUES ('8', '新加坡');
INSERT INTO videocountry VALUES ('9', '法国');

-- ----------------------------
-- Table structure for `videoformat`
-- ----------------------------
DROP TABLE IF EXISTS `videoformat`;
CREATE TABLE `videoformat` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(16) NOT NULL,
  `fullName` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of videoformat
-- ----------------------------
INSERT INTO videoformat VALUES ('1', 'DVD', null);
INSERT INTO videoformat VALUES ('2', 'HD', null);
INSERT INTO videoformat VALUES ('3', 'BD', '蓝光');
INSERT INTO videoformat VALUES ('4', 'CD', null);

-- ----------------------------
-- Table structure for `videotovolume`
-- ----------------------------
DROP TABLE IF EXISTS `videotovolume`;
CREATE TABLE `videotovolume` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `videoId` bigint(20) unsigned NOT NULL,
  `volumeId` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of videotovolume
-- ----------------------------

-- ----------------------------
-- Table structure for `videotype`
-- ----------------------------
DROP TABLE IF EXISTS `videotype`;
CREATE TABLE `videotype` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of videotype
-- ----------------------------
INSERT INTO videotype VALUES ('11', '动作');
INSERT INTO videotype VALUES ('12', '喜剧');
INSERT INTO videotype VALUES ('13', '爱情');
INSERT INTO videotype VALUES ('14', '科幻');
INSERT INTO videotype VALUES ('15', '战争');
INSERT INTO videotype VALUES ('16', '剧情');
INSERT INTO videotype VALUES ('17', '动画');
INSERT INTO videotype VALUES ('18', '综艺');
INSERT INTO videotype VALUES ('19', '古装');
INSERT INTO videotype VALUES ('20', '现代');
INSERT INTO videotype VALUES ('21', '武侠');
INSERT INTO videotype VALUES ('22', '都市');
INSERT INTO videotype VALUES ('23', '悬疑');
INSERT INTO videotype VALUES ('24', '言情');
INSERT INTO videotype VALUES ('25', '神话');
INSERT INTO videotype VALUES ('26', '历史');

-- ----------------------------
-- Table structure for `volume`
-- ----------------------------
DROP TABLE IF EXISTS `volume`;
CREATE TABLE `volume` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `url` varchar(1024) NOT NULL COMMENT '视频连接',
  `belongto` bigint(20) unsigned NOT NULL COMMENT '视频属于哪个电影或电视剧',
  `volume` varchar(32) NOT NULL COMMENT '电影集',
  `player` int(10) unsigned NOT NULL COMMENT '使用的播放方式',
  `description` text COMMENT '本集介绍，可以为空',
  `md5` varchar(64) DEFAULT NULL COMMENT 'url的md5表示',
  PRIMARY KEY (`id`),
  KEY `volumindex` (`belongto`,`md5`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of volume
-- ----------------------------

-- ----------------------------
-- Table structure for `weekvideolist`
-- ----------------------------
DROP TABLE IF EXISTS `weekvideolist`;
CREATE TABLE `weekvideolist` (
  `week` int(10) unsigned NOT NULL,
  `type` int(10) unsigned NOT NULL,
  `content` blob NOT NULL,
  UNIQUE KEY `NewIndex1` (`week`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of weekvideolist
-- ----------------------------
