/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50624
Source Host           : localhost:3306
Source Database       : ssm-study

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2022-05-15 14:41:52
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `t_goods`
-- ----------------------------
DROP TABLE IF EXISTS `t_goods`;
CREATE TABLE `t_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '��Ʒid',
  `goods_name` varchar(16) NOT NULL DEFAULT 'null' COMMENT '��Ʒ����',
  `goods_title` varchar(64) NOT NULL DEFAULT 'null' COMMENT '��Ʒ����',
  `goods_img` varchar(64) NOT NULL DEFAULT 'null' COMMENT '��ƷͼƬ',
  `goods_detail` longtext COMMENT '��Ʒ����',
  `goods_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '��Ʒ����',
  `goods_stock` int(11) NOT NULL DEFAULT '0' COMMENT '��Ʒ��棬-1��ʾ������',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_goods
-- ----------------------------
INSERT INTO `t_goods` VALUES ('1', 'iphone12', 'iphone12 64g', '/img/1.jpg', 'iphone12 64g', '6299.00', '100');
INSERT INTO `t_goods` VALUES ('2', 'iphone12 pro', 'iphone12 pro128g', '/img/2.jpg', 'iphone12 pro 128g', '9299.00', '100');

-- ----------------------------
-- Table structure for `t_order`
-- ----------------------------
DROP TABLE IF EXISTS `t_order`;
CREATE TABLE `t_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '����id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '�û�id',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '��Ʒid',
  `delivery_addr_id` bigint(20) DEFAULT NULL COMMENT '�ջ���ַid',
  `goods_name` varchar(16) DEFAULT NULL COMMENT '��Ʒ����',
  `goods_count` int(11) DEFAULT '0' COMMENT '��Ʒ����',
  `goods_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '��Ʒ����',
  `order_channel` tinyint(4) DEFAULT '0' COMMENT '1-pc,2-android,3-ios',
  `status` tinyint(4) DEFAULT '0' COMMENT '����״̬��0-�½�δ֧����1-��֧����2-�ѷ�����3-���ջ���4-���˿5-�����',
  `create_date` datetime DEFAULT NULL COMMENT '�����Ĵ���ʱ��',
  `pay_date` datetime DEFAULT NULL COMMENT '֧��ʱ��',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=390 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_order
-- ----------------------------

-- ----------------------------
-- Table structure for `t_seckill_goods`
-- ----------------------------
DROP TABLE IF EXISTS `t_seckill_goods`;
CREATE TABLE `t_seckill_goods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '��ɱ��Ʒid',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '��Ʒid',
  `seckill_price` decimal(10,2) DEFAULT NULL COMMENT '��ɱ��',
  `stock_count` bigint(20) DEFAULT NULL COMMENT '�������',
  `start_date` datetime DEFAULT NULL COMMENT '��ɱ��ʼʱ��',
  `end_date` datetime DEFAULT NULL COMMENT '��ɱ����ʱ��',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_seckill_goods
-- ----------------------------
INSERT INTO `t_seckill_goods` VALUES ('1', '1', '629.00', '10', '2022-04-26 20:41:49', '2022-04-27 20:41:55');
INSERT INTO `t_seckill_goods` VALUES ('2', '2', '929.00', '10', '2022-04-26 20:41:49', '2022-04-27 20:41:55');

-- ----------------------------
-- Table structure for `t_seckill_order`
-- ----------------------------
DROP TABLE IF EXISTS `t_seckill_order`;
CREATE TABLE `t_seckill_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '��ɱ����id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '�û�id',
  `order_id` bigint(20) DEFAULT NULL COMMENT '����id',
  `goods_id` bigint(20) DEFAULT NULL COMMENT '��Ʒid',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_seckill_order
-- ----------------------------

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(32) NOT NULL AUTO_INCREMENT COMMENT '����',
  `name` varchar(32) DEFAULT NULL COMMENT '����',
  `age` int(3) DEFAULT NULL COMMENT '����',
  `type` varchar(8) DEFAULT NULL COMMENT '����',
  `updatetime` datetime DEFAULT NULL COMMENT '�޸�ʱ��',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', '321', '1', 'ѧ��', '2022-03-08 08:37:28');
INSERT INTO `user` VALUES ('2', 'С��', '5', 'Ӥ��', '2022-03-09 12:35:37');
