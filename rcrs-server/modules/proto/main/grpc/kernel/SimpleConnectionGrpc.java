package kernel;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.28.0)",
    comments = "Source: RCRS.proto")
public final class SimpleConnectionGrpc {

  private SimpleConnectionGrpc() {}

  public static final String SERVICE_NAME = "simpleRCRS.SimpleConnection";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<kernel.AgentProto,
      kernel.ActionType> getSetActionTypeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SetActionType",
      requestType = kernel.AgentProto.class,
      responseType = kernel.ActionType.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<kernel.AgentProto,
      kernel.ActionType> getSetActionTypeMethod() {
    io.grpc.MethodDescriptor<kernel.AgentProto, kernel.ActionType> getSetActionTypeMethod;
    if ((getSetActionTypeMethod = SimpleConnectionGrpc.getSetActionTypeMethod) == null) {
      synchronized (SimpleConnectionGrpc.class) {
        if ((getSetActionTypeMethod = SimpleConnectionGrpc.getSetActionTypeMethod) == null) {
          SimpleConnectionGrpc.getSetActionTypeMethod = getSetActionTypeMethod =
              io.grpc.MethodDescriptor.<kernel.AgentProto, kernel.ActionType>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SetActionType"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  kernel.AgentProto.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  kernel.ActionType.getDefaultInstance()))
              .setSchemaDescriptor(new SimpleConnectionMethodDescriptorSupplier("SetActionType"))
              .build();
        }
      }
    }
    return getSetActionTypeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<kernel.Check,
      kernel.Move> getSetMoveMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SetMove",
      requestType = kernel.Check.class,
      responseType = kernel.Move.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<kernel.Check,
      kernel.Move> getSetMoveMethod() {
    io.grpc.MethodDescriptor<kernel.Check, kernel.Move> getSetMoveMethod;
    if ((getSetMoveMethod = SimpleConnectionGrpc.getSetMoveMethod) == null) {
      synchronized (SimpleConnectionGrpc.class) {
        if ((getSetMoveMethod = SimpleConnectionGrpc.getSetMoveMethod) == null) {
          SimpleConnectionGrpc.getSetMoveMethod = getSetMoveMethod =
              io.grpc.MethodDescriptor.<kernel.Check, kernel.Move>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SetMove"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  kernel.Check.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  kernel.Move.getDefaultInstance()))
              .setSchemaDescriptor(new SimpleConnectionMethodDescriptorSupplier("SetMove"))
              .build();
        }
      }
    }
    return getSetMoveMethod;
  }

  private static volatile io.grpc.MethodDescriptor<kernel.WorldInfoProto,
      kernel.ActionType> getRunTimestepMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RunTimestep",
      requestType = kernel.WorldInfoProto.class,
      responseType = kernel.ActionType.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<kernel.WorldInfoProto,
      kernel.ActionType> getRunTimestepMethod() {
    io.grpc.MethodDescriptor<kernel.WorldInfoProto, kernel.ActionType> getRunTimestepMethod;
    if ((getRunTimestepMethod = SimpleConnectionGrpc.getRunTimestepMethod) == null) {
      synchronized (SimpleConnectionGrpc.class) {
        if ((getRunTimestepMethod = SimpleConnectionGrpc.getRunTimestepMethod) == null) {
          SimpleConnectionGrpc.getRunTimestepMethod = getRunTimestepMethod =
              io.grpc.MethodDescriptor.<kernel.WorldInfoProto, kernel.ActionType>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RunTimestep"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  kernel.WorldInfoProto.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  kernel.ActionType.getDefaultInstance()))
              .setSchemaDescriptor(new SimpleConnectionMethodDescriptorSupplier("RunTimestep"))
              .build();
        }
      }
    }
    return getRunTimestepMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SimpleConnectionStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SimpleConnectionStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SimpleConnectionStub>() {
        @java.lang.Override
        public SimpleConnectionStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SimpleConnectionStub(channel, callOptions);
        }
      };
    return SimpleConnectionStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SimpleConnectionBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SimpleConnectionBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SimpleConnectionBlockingStub>() {
        @java.lang.Override
        public SimpleConnectionBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SimpleConnectionBlockingStub(channel, callOptions);
        }
      };
    return SimpleConnectionBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static SimpleConnectionFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<SimpleConnectionFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<SimpleConnectionFutureStub>() {
        @java.lang.Override
        public SimpleConnectionFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new SimpleConnectionFutureStub(channel, callOptions);
        }
      };
    return SimpleConnectionFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class SimpleConnectionImplBase implements io.grpc.BindableService {

    /**
     */
    public void setActionType(kernel.AgentProto request,
        io.grpc.stub.StreamObserver<kernel.ActionType> responseObserver) {
      asyncUnimplementedUnaryCall(getSetActionTypeMethod(), responseObserver);
    }

    /**
     */
    public void setMove(kernel.Check request,
        io.grpc.stub.StreamObserver<kernel.Move> responseObserver) {
      asyncUnimplementedUnaryCall(getSetMoveMethod(), responseObserver);
    }

    /**
     */
    public void runTimestep(kernel.WorldInfoProto request,
        io.grpc.stub.StreamObserver<kernel.ActionType> responseObserver) {
      asyncUnimplementedUnaryCall(getRunTimestepMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getSetActionTypeMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                kernel.AgentProto,
                kernel.ActionType>(
                  this, METHODID_SET_ACTION_TYPE)))
          .addMethod(
            getSetMoveMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                kernel.Check,
                kernel.Move>(
                  this, METHODID_SET_MOVE)))
          .addMethod(
            getRunTimestepMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                kernel.WorldInfoProto,
                kernel.ActionType>(
                  this, METHODID_RUN_TIMESTEP)))
          .build();
    }
  }

  /**
   */
  public static final class SimpleConnectionStub extends io.grpc.stub.AbstractAsyncStub<SimpleConnectionStub> {
    private SimpleConnectionStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SimpleConnectionStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SimpleConnectionStub(channel, callOptions);
    }

    /**
     */
    public void setActionType(kernel.AgentProto request,
        io.grpc.stub.StreamObserver<kernel.ActionType> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSetActionTypeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setMove(kernel.Check request,
        io.grpc.stub.StreamObserver<kernel.Move> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getSetMoveMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void runTimestep(kernel.WorldInfoProto request,
        io.grpc.stub.StreamObserver<kernel.ActionType> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRunTimestepMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class SimpleConnectionBlockingStub extends io.grpc.stub.AbstractBlockingStub<SimpleConnectionBlockingStub> {
    private SimpleConnectionBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SimpleConnectionBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SimpleConnectionBlockingStub(channel, callOptions);
    }

    /**
     */
    public kernel.ActionType setActionType(kernel.AgentProto request) {
      return blockingUnaryCall(
          getChannel(), getSetActionTypeMethod(), getCallOptions(), request);
    }

    /**
     */
    public kernel.Move setMove(kernel.Check request) {
      return blockingUnaryCall(
          getChannel(), getSetMoveMethod(), getCallOptions(), request);
    }

    /**
     */
    public kernel.ActionType runTimestep(kernel.WorldInfoProto request) {
      return blockingUnaryCall(
          getChannel(), getRunTimestepMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class SimpleConnectionFutureStub extends io.grpc.stub.AbstractFutureStub<SimpleConnectionFutureStub> {
    private SimpleConnectionFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SimpleConnectionFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new SimpleConnectionFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<kernel.ActionType> setActionType(
        kernel.AgentProto request) {
      return futureUnaryCall(
          getChannel().newCall(getSetActionTypeMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<kernel.Move> setMove(
        kernel.Check request) {
      return futureUnaryCall(
          getChannel().newCall(getSetMoveMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<kernel.ActionType> runTimestep(
        kernel.WorldInfoProto request) {
      return futureUnaryCall(
          getChannel().newCall(getRunTimestepMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SET_ACTION_TYPE = 0;
  private static final int METHODID_SET_MOVE = 1;
  private static final int METHODID_RUN_TIMESTEP = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final SimpleConnectionImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(SimpleConnectionImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_SET_ACTION_TYPE:
          serviceImpl.setActionType((kernel.AgentProto) request,
              (io.grpc.stub.StreamObserver<kernel.ActionType>) responseObserver);
          break;
        case METHODID_SET_MOVE:
          serviceImpl.setMove((kernel.Check) request,
              (io.grpc.stub.StreamObserver<kernel.Move>) responseObserver);
          break;
        case METHODID_RUN_TIMESTEP:
          serviceImpl.runTimestep((kernel.WorldInfoProto) request,
              (io.grpc.stub.StreamObserver<kernel.ActionType>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class SimpleConnectionBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    SimpleConnectionBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return kernel.RCRSProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("SimpleConnection");
    }
  }

  private static final class SimpleConnectionFileDescriptorSupplier
      extends SimpleConnectionBaseDescriptorSupplier {
    SimpleConnectionFileDescriptorSupplier() {}
  }

  private static final class SimpleConnectionMethodDescriptorSupplier
      extends SimpleConnectionBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    SimpleConnectionMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (SimpleConnectionGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new SimpleConnectionFileDescriptorSupplier())
              .addMethod(getSetActionTypeMethod())
              .addMethod(getSetMoveMethod())
              .addMethod(getRunTimestepMethod())
              .build();
        }
      }
    }
    return result;
  }
}
