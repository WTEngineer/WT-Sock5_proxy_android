/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.socks5proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Inet6Address;

import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;

/**
 * @author KDark
 */
enum Addr {
    V4,
    V6,
    DOMAIN
}

public class Socks5Server {

    private static final Logger log = Logger.getLogger(Socks5Server.class.getName());
//    private static final byte[] MAGIC_FLAG = {0x37, 0x37};

    public static void main(String[] args) throws InterruptedException {
        // Set up the server
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new Socks5ServerInitializer());

            ChannelFuture future = bootstrap.bind(1080).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    
}


class TcpTransferHandler extends ChannelInboundHandlerAdapter {

    private final String address;
    private final Addr addr;
    private final int port;

    public TcpTransferHandler(String address, Addr addr, int port) {
        this.address = address;
        this.addr = addr;
        this.port = port;
    }





    private static class RelayHandler extends ChannelInboundHandlerAdapter {
        private final Channel relayChannel;

        RelayHandler(Channel relayChannel) {
            this.relayChannel = relayChannel;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            relayChannel.writeAndFlush(msg);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            if (relayChannel.isActive()) {
                relayChannel.close();
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
}