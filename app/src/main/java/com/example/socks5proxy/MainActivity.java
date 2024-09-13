package com.example.socks5proxy;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.StandardCharsets;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.buffer.ByteBuf;

public class MainActivity extends AppCompatActivity {

    private NioEventLoopGroup group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProxyServer();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socks5Server.main(null);  // Start the SOCKS5 server
                    //WT_This thread starts the SOCKS5 server (Socks5Server.main(null)), so the server runs concurrently with the app.
                } catch (InterruptedException e) {
                    Log.e("Socks5Server", "Server interrupted", e);
                }
            }
        }).start();
    }

    private void startProxyServer() {           //Starts a new connection to the proxy server (SOCKS5 server or another server).
        group = new NioEventLoopGroup();        //A thread pool that handles network events.

//        String host = "188.245.104.81";
        String host = "192.168.8.165";
        int port = 8000;

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new ByteInboundHandler(host, port, group));
                    }
                });

        ChannelFuture future = bootstrap.connect(host, port);
        future.addListener(f -> {
            if (f.isSuccess()) {
                runOnUiThread(() -> {
                    // Update UI if needed, e.g., show a Toast
                    // Toast.makeText(MainActivity.this, "Connected to " + host + ":" + port, Toast.LENGTH_SHORT).show();
                });
            } else {
                runOnUiThread(() -> {
                    // Update UI if needed, e.g., show an error Toast
                    // Toast.makeText(MainActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (group != null) {
            group.shutdownGracefully();
        }
    }

    private class ByteInboundHandler extends SimpleChannelInboundHandler<ByteBuf> {     //This class extends SimpleChannelInboundHandler<ByteBuf> to handle incoming data
        private final String host;
        private final int port;
        private final NioEventLoopGroup group;

        public ByteInboundHandler(String host, int port, NioEventLoopGroup group) {
            this.host = host;
            this.port = port;
            this.group = group;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) throws Exception {       //This method is used to handle incoming data from a network connection.
        }
    }
}