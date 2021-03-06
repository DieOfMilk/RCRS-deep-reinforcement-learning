// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: RCRS.proto

package kernel;

/**
 * Protobuf type {@code simpleRCRS.BlockadeProto}
 */
public  final class BlockadeProto extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:simpleRCRS.BlockadeProto)
    BlockadeProtoOrBuilder {
private static final long serialVersionUID = 0L;
  // Use BlockadeProto.newBuilder() to construct.
  private BlockadeProto(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private BlockadeProto() {
    uRN_ = "";
    apexList_ = emptyIntList();
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new BlockadeProto();
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private BlockadeProto(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    int mutable_bitField0_ = 0;
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 10: {
            java.lang.String s = input.readStringRequireUtf8();

            uRN_ = s;
            break;
          }
          case 16: {

            iD_ = input.readInt32();
            break;
          }
          case 24: {

            x_ = input.readInt32();
            break;
          }
          case 32: {

            y_ = input.readInt32();
            break;
          }
          case 40: {

            cost_ = input.readInt32();
            break;
          }
          case 48: {

            positionID_ = input.readInt32();
            break;
          }
          case 56: {
            if (!((mutable_bitField0_ & 0x00000001) != 0)) {
              apexList_ = newIntList();
              mutable_bitField0_ |= 0x00000001;
            }
            apexList_.addInt(input.readInt32());
            break;
          }
          case 58: {
            int length = input.readRawVarint32();
            int limit = input.pushLimit(length);
            if (!((mutable_bitField0_ & 0x00000001) != 0) && input.getBytesUntilLimit() > 0) {
              apexList_ = newIntList();
              mutable_bitField0_ |= 0x00000001;
            }
            while (input.getBytesUntilLimit() > 0) {
              apexList_.addInt(input.readInt32());
            }
            input.popLimit(limit);
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      if (((mutable_bitField0_ & 0x00000001) != 0)) {
        apexList_.makeImmutable(); // C
      }
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return kernel.RCRSProto.internal_static_simpleRCRS_BlockadeProto_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return kernel.RCRSProto.internal_static_simpleRCRS_BlockadeProto_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            kernel.BlockadeProto.class, kernel.BlockadeProto.Builder.class);
  }

  public static final int URN_FIELD_NUMBER = 1;
  private volatile java.lang.Object uRN_;
  /**
   * <code>string uRN = 1;</code>
   * @return The uRN.
   */
  public java.lang.String getURN() {
    java.lang.Object ref = uRN_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      uRN_ = s;
      return s;
    }
  }
  /**
   * <code>string uRN = 1;</code>
   * @return The bytes for uRN.
   */
  public com.google.protobuf.ByteString
      getURNBytes() {
    java.lang.Object ref = uRN_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      uRN_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int ID_FIELD_NUMBER = 2;
  private int iD_;
  /**
   * <code>int32 iD = 2;</code>
   * @return The iD.
   */
  public int getID() {
    return iD_;
  }

  public static final int X_FIELD_NUMBER = 3;
  private int x_;
  /**
   * <code>int32 x = 3;</code>
   * @return The x.
   */
  public int getX() {
    return x_;
  }

  public static final int Y_FIELD_NUMBER = 4;
  private int y_;
  /**
   * <code>int32 y = 4;</code>
   * @return The y.
   */
  public int getY() {
    return y_;
  }

  public static final int COST_FIELD_NUMBER = 5;
  private int cost_;
  /**
   * <code>int32 cost = 5;</code>
   * @return The cost.
   */
  public int getCost() {
    return cost_;
  }

  public static final int POSITIONID_FIELD_NUMBER = 6;
  private int positionID_;
  /**
   * <code>int32 positionID = 6;</code>
   * @return The positionID.
   */
  public int getPositionID() {
    return positionID_;
  }

  public static final int APEXLIST_FIELD_NUMBER = 7;
  private com.google.protobuf.Internal.IntList apexList_;
  /**
   * <code>repeated int32 apexList = 7;</code>
   * @return A list containing the apexList.
   */
  public java.util.List<java.lang.Integer>
      getApexListList() {
    return apexList_;
  }
  /**
   * <code>repeated int32 apexList = 7;</code>
   * @return The count of apexList.
   */
  public int getApexListCount() {
    return apexList_.size();
  }
  /**
   * <code>repeated int32 apexList = 7;</code>
   * @param index The index of the element to return.
   * @return The apexList at the given index.
   */
  public int getApexList(int index) {
    return apexList_.getInt(index);
  }
  private int apexListMemoizedSerializedSize = -1;

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    getSerializedSize();
    if (!getURNBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, uRN_);
    }
    if (iD_ != 0) {
      output.writeInt32(2, iD_);
    }
    if (x_ != 0) {
      output.writeInt32(3, x_);
    }
    if (y_ != 0) {
      output.writeInt32(4, y_);
    }
    if (cost_ != 0) {
      output.writeInt32(5, cost_);
    }
    if (positionID_ != 0) {
      output.writeInt32(6, positionID_);
    }
    if (getApexListList().size() > 0) {
      output.writeUInt32NoTag(58);
      output.writeUInt32NoTag(apexListMemoizedSerializedSize);
    }
    for (int i = 0; i < apexList_.size(); i++) {
      output.writeInt32NoTag(apexList_.getInt(i));
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getURNBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, uRN_);
    }
    if (iD_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(2, iD_);
    }
    if (x_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(3, x_);
    }
    if (y_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(4, y_);
    }
    if (cost_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(5, cost_);
    }
    if (positionID_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(6, positionID_);
    }
    {
      int dataSize = 0;
      for (int i = 0; i < apexList_.size(); i++) {
        dataSize += com.google.protobuf.CodedOutputStream
          .computeInt32SizeNoTag(apexList_.getInt(i));
      }
      size += dataSize;
      if (!getApexListList().isEmpty()) {
        size += 1;
        size += com.google.protobuf.CodedOutputStream
            .computeInt32SizeNoTag(dataSize);
      }
      apexListMemoizedSerializedSize = dataSize;
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof kernel.BlockadeProto)) {
      return super.equals(obj);
    }
    kernel.BlockadeProto other = (kernel.BlockadeProto) obj;

    if (!getURN()
        .equals(other.getURN())) return false;
    if (getID()
        != other.getID()) return false;
    if (getX()
        != other.getX()) return false;
    if (getY()
        != other.getY()) return false;
    if (getCost()
        != other.getCost()) return false;
    if (getPositionID()
        != other.getPositionID()) return false;
    if (!getApexListList()
        .equals(other.getApexListList())) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + URN_FIELD_NUMBER;
    hash = (53 * hash) + getURN().hashCode();
    hash = (37 * hash) + ID_FIELD_NUMBER;
    hash = (53 * hash) + getID();
    hash = (37 * hash) + X_FIELD_NUMBER;
    hash = (53 * hash) + getX();
    hash = (37 * hash) + Y_FIELD_NUMBER;
    hash = (53 * hash) + getY();
    hash = (37 * hash) + COST_FIELD_NUMBER;
    hash = (53 * hash) + getCost();
    hash = (37 * hash) + POSITIONID_FIELD_NUMBER;
    hash = (53 * hash) + getPositionID();
    if (getApexListCount() > 0) {
      hash = (37 * hash) + APEXLIST_FIELD_NUMBER;
      hash = (53 * hash) + getApexListList().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static kernel.BlockadeProto parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static kernel.BlockadeProto parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static kernel.BlockadeProto parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static kernel.BlockadeProto parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static kernel.BlockadeProto parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static kernel.BlockadeProto parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static kernel.BlockadeProto parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static kernel.BlockadeProto parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static kernel.BlockadeProto parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static kernel.BlockadeProto parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static kernel.BlockadeProto parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static kernel.BlockadeProto parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(kernel.BlockadeProto prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code simpleRCRS.BlockadeProto}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:simpleRCRS.BlockadeProto)
      kernel.BlockadeProtoOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return kernel.RCRSProto.internal_static_simpleRCRS_BlockadeProto_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return kernel.RCRSProto.internal_static_simpleRCRS_BlockadeProto_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              kernel.BlockadeProto.class, kernel.BlockadeProto.Builder.class);
    }

    // Construct using kernel.BlockadeProto.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      uRN_ = "";

      iD_ = 0;

      x_ = 0;

      y_ = 0;

      cost_ = 0;

      positionID_ = 0;

      apexList_ = emptyIntList();
      bitField0_ = (bitField0_ & ~0x00000001);
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return kernel.RCRSProto.internal_static_simpleRCRS_BlockadeProto_descriptor;
    }

    @java.lang.Override
    public kernel.BlockadeProto getDefaultInstanceForType() {
      return kernel.BlockadeProto.getDefaultInstance();
    }

    @java.lang.Override
    public kernel.BlockadeProto build() {
      kernel.BlockadeProto result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public kernel.BlockadeProto buildPartial() {
      kernel.BlockadeProto result = new kernel.BlockadeProto(this);
      int from_bitField0_ = bitField0_;
      result.uRN_ = uRN_;
      result.iD_ = iD_;
      result.x_ = x_;
      result.y_ = y_;
      result.cost_ = cost_;
      result.positionID_ = positionID_;
      if (((bitField0_ & 0x00000001) != 0)) {
        apexList_.makeImmutable();
        bitField0_ = (bitField0_ & ~0x00000001);
      }
      result.apexList_ = apexList_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof kernel.BlockadeProto) {
        return mergeFrom((kernel.BlockadeProto)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(kernel.BlockadeProto other) {
      if (other == kernel.BlockadeProto.getDefaultInstance()) return this;
      if (!other.getURN().isEmpty()) {
        uRN_ = other.uRN_;
        onChanged();
      }
      if (other.getID() != 0) {
        setID(other.getID());
      }
      if (other.getX() != 0) {
        setX(other.getX());
      }
      if (other.getY() != 0) {
        setY(other.getY());
      }
      if (other.getCost() != 0) {
        setCost(other.getCost());
      }
      if (other.getPositionID() != 0) {
        setPositionID(other.getPositionID());
      }
      if (!other.apexList_.isEmpty()) {
        if (apexList_.isEmpty()) {
          apexList_ = other.apexList_;
          bitField0_ = (bitField0_ & ~0x00000001);
        } else {
          ensureApexListIsMutable();
          apexList_.addAll(other.apexList_);
        }
        onChanged();
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      kernel.BlockadeProto parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (kernel.BlockadeProto) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private java.lang.Object uRN_ = "";
    /**
     * <code>string uRN = 1;</code>
     * @return The uRN.
     */
    public java.lang.String getURN() {
      java.lang.Object ref = uRN_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        uRN_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>string uRN = 1;</code>
     * @return The bytes for uRN.
     */
    public com.google.protobuf.ByteString
        getURNBytes() {
      java.lang.Object ref = uRN_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        uRN_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string uRN = 1;</code>
     * @param value The uRN to set.
     * @return This builder for chaining.
     */
    public Builder setURN(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      uRN_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string uRN = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearURN() {
      
      uRN_ = getDefaultInstance().getURN();
      onChanged();
      return this;
    }
    /**
     * <code>string uRN = 1;</code>
     * @param value The bytes for uRN to set.
     * @return This builder for chaining.
     */
    public Builder setURNBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      uRN_ = value;
      onChanged();
      return this;
    }

    private int iD_ ;
    /**
     * <code>int32 iD = 2;</code>
     * @return The iD.
     */
    public int getID() {
      return iD_;
    }
    /**
     * <code>int32 iD = 2;</code>
     * @param value The iD to set.
     * @return This builder for chaining.
     */
    public Builder setID(int value) {
      
      iD_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 iD = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearID() {
      
      iD_ = 0;
      onChanged();
      return this;
    }

    private int x_ ;
    /**
     * <code>int32 x = 3;</code>
     * @return The x.
     */
    public int getX() {
      return x_;
    }
    /**
     * <code>int32 x = 3;</code>
     * @param value The x to set.
     * @return This builder for chaining.
     */
    public Builder setX(int value) {
      
      x_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 x = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearX() {
      
      x_ = 0;
      onChanged();
      return this;
    }

    private int y_ ;
    /**
     * <code>int32 y = 4;</code>
     * @return The y.
     */
    public int getY() {
      return y_;
    }
    /**
     * <code>int32 y = 4;</code>
     * @param value The y to set.
     * @return This builder for chaining.
     */
    public Builder setY(int value) {
      
      y_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 y = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearY() {
      
      y_ = 0;
      onChanged();
      return this;
    }

    private int cost_ ;
    /**
     * <code>int32 cost = 5;</code>
     * @return The cost.
     */
    public int getCost() {
      return cost_;
    }
    /**
     * <code>int32 cost = 5;</code>
     * @param value The cost to set.
     * @return This builder for chaining.
     */
    public Builder setCost(int value) {
      
      cost_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 cost = 5;</code>
     * @return This builder for chaining.
     */
    public Builder clearCost() {
      
      cost_ = 0;
      onChanged();
      return this;
    }

    private int positionID_ ;
    /**
     * <code>int32 positionID = 6;</code>
     * @return The positionID.
     */
    public int getPositionID() {
      return positionID_;
    }
    /**
     * <code>int32 positionID = 6;</code>
     * @param value The positionID to set.
     * @return This builder for chaining.
     */
    public Builder setPositionID(int value) {
      
      positionID_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 positionID = 6;</code>
     * @return This builder for chaining.
     */
    public Builder clearPositionID() {
      
      positionID_ = 0;
      onChanged();
      return this;
    }

    private com.google.protobuf.Internal.IntList apexList_ = emptyIntList();
    private void ensureApexListIsMutable() {
      if (!((bitField0_ & 0x00000001) != 0)) {
        apexList_ = mutableCopy(apexList_);
        bitField0_ |= 0x00000001;
       }
    }
    /**
     * <code>repeated int32 apexList = 7;</code>
     * @return A list containing the apexList.
     */
    public java.util.List<java.lang.Integer>
        getApexListList() {
      return ((bitField0_ & 0x00000001) != 0) ?
               java.util.Collections.unmodifiableList(apexList_) : apexList_;
    }
    /**
     * <code>repeated int32 apexList = 7;</code>
     * @return The count of apexList.
     */
    public int getApexListCount() {
      return apexList_.size();
    }
    /**
     * <code>repeated int32 apexList = 7;</code>
     * @param index The index of the element to return.
     * @return The apexList at the given index.
     */
    public int getApexList(int index) {
      return apexList_.getInt(index);
    }
    /**
     * <code>repeated int32 apexList = 7;</code>
     * @param index The index to set the value at.
     * @param value The apexList to set.
     * @return This builder for chaining.
     */
    public Builder setApexList(
        int index, int value) {
      ensureApexListIsMutable();
      apexList_.setInt(index, value);
      onChanged();
      return this;
    }
    /**
     * <code>repeated int32 apexList = 7;</code>
     * @param value The apexList to add.
     * @return This builder for chaining.
     */
    public Builder addApexList(int value) {
      ensureApexListIsMutable();
      apexList_.addInt(value);
      onChanged();
      return this;
    }
    /**
     * <code>repeated int32 apexList = 7;</code>
     * @param values The apexList to add.
     * @return This builder for chaining.
     */
    public Builder addAllApexList(
        java.lang.Iterable<? extends java.lang.Integer> values) {
      ensureApexListIsMutable();
      com.google.protobuf.AbstractMessageLite.Builder.addAll(
          values, apexList_);
      onChanged();
      return this;
    }
    /**
     * <code>repeated int32 apexList = 7;</code>
     * @return This builder for chaining.
     */
    public Builder clearApexList() {
      apexList_ = emptyIntList();
      bitField0_ = (bitField0_ & ~0x00000001);
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:simpleRCRS.BlockadeProto)
  }

  // @@protoc_insertion_point(class_scope:simpleRCRS.BlockadeProto)
  private static final kernel.BlockadeProto DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new kernel.BlockadeProto();
  }

  public static kernel.BlockadeProto getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<BlockadeProto>
      PARSER = new com.google.protobuf.AbstractParser<BlockadeProto>() {
    @java.lang.Override
    public BlockadeProto parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new BlockadeProto(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<BlockadeProto> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<BlockadeProto> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public kernel.BlockadeProto getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

