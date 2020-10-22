// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: firebrigade.proto

package adf.sample.tactics;

public final class firebrigadeProto {
  private firebrigadeProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_simpleRCRS_ActionType_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_simpleRCRS_ActionType_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_simpleRCRS_Move_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_simpleRCRS_Move_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_simpleRCRS_Check_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_simpleRCRS_Check_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_simpleRCRS_EdgeProto_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_simpleRCRS_EdgeProto_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_simpleRCRS_AreaProto_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_simpleRCRS_AreaProto_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_simpleRCRS_HumanProto_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_simpleRCRS_HumanProto_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_simpleRCRS_BlockadeProto_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_simpleRCRS_BlockadeProto_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_simpleRCRS_ElseProto_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_simpleRCRS_ElseProto_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_simpleRCRS_WorldInfoProto_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_simpleRCRS_WorldInfoProto_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\021firebrigade.proto\022\nsimpleRCRS\"6\n\nActio" +
      "nType\022\022\n\nactionType\030\001 \001(\005\022\t\n\001x\030\002 \001(\001\022\t\n\001" +
      "y\030\003 \001(\001\"\034\n\004Move\022\t\n\001x\030\001 \001(\001\022\t\n\001y\030\002 \001(\001\"\026\n" +
      "\005Check\022\r\n\005check\030\001 \001(\005\"Z\n\tEdgeProto\022\016\n\006st" +
      "artX\030\001 \001(\005\022\016\n\006startY\030\002 \001(\005\022\014\n\004endX\030\003 \001(\005" +
      "\022\014\n\004endY\030\004 \001(\005\022\021\n\tneighbour\030\005 \001(\005\"\366\002\n\tAr" +
      "eaProto\022\013\n\003uRN\030\001 \001(\t\022\n\n\002iD\030\002 \001(\005\022\t\n\001x\030\003 " +
      "\001(\005\022\t\n\001y\030\004 \001(\005\022$\n\005edges\030\005 \003(\0132\025.simpleRC" +
      "RS.EdgeProto\022\021\n\tblockades\030\006 \003(\005\022\020\n\010apexL" +
      "ist\030\007 \003(\005\022\022\n\nneighbours\030\010 \003(\005\022\016\n\006floors\030" +
      "\t \001(\005\022\020\n\010ignition\030\n \001(\010\022\021\n\tfieryness\030\013 \001" +
      "(\005\022\022\n\nbrokenness\030\014 \001(\005\022\024\n\014buildingCode\030\r" +
      " \001(\005\022\032\n\022buildingAttributes\030\016 \001(\005\022\022\n\ngrou" +
      "ndArea\030\017 \001(\005\022\021\n\ttotalArea\030\020 \001(\005\022\023\n\013tempe" +
      "rature\030\021 \001(\005\022\022\n\nimportance\030\022 \001(\005\022\020\n\010isOn" +
      "Fire\030\023 \001(\010\"\343\001\n\nHumanProto\022\013\n\003uRN\030\001 \001(\t\022\n" +
      "\n\002iD\030\002 \001(\005\022\t\n\001x\030\003 \001(\005\022\t\n\001y\030\004 \001(\005\022\022\n\nposi" +
      "tionID\030\005 \001(\005\022\027\n\017positionHistory\030\006 \003(\005\022\026\n" +
      "\016travelDistance\030\007 \001(\005\022\022\n\nburiedness\030\010 \001(" +
      "\005\022\016\n\006damage\030\t \001(\005\022\n\n\002hP\030\n \001(\005\022\017\n\007stamina" +
      "\030\013 \001(\005\022\021\n\tdirection\030\014 \001(\005\022\r\n\005water\030\r \001(\005" +
      "\"r\n\rBlockadeProto\022\013\n\003uRN\030\001 \001(\t\022\n\n\002iD\030\002 \001" +
      "(\005\022\t\n\001x\030\003 \001(\005\022\t\n\001y\030\004 \001(\005\022\014\n\004cost\030\005 \001(\005\022\022" +
      "\n\npositionID\030\006 \001(\005\022\020\n\010apexList\030\007 \003(\005\"$\n\t" +
      "ElseProto\022\013\n\003uRN\030\001 \001(\t\022\n\n\002iD\030\002 \001(\005\"\300\001\n\016W" +
      "orldInfoProto\022$\n\005areas\030\001 \003(\0132\025.simpleRCR" +
      "S.AreaProto\022&\n\006humans\030\002 \003(\0132\026.simpleRCRS" +
      ".HumanProto\022,\n\tblockades\030\003 \003(\0132\031.simpleR" +
      "CRS.BlockadeProto\022$\n\005elses\030\004 \003(\0132\025.simpl" +
      "eRCRS.ElseProto\022\014\n\004time\030\005 \001(\0052\307\001\n\020Simple" +
      "Connection\022<\n\rSetActionType\022\021.simpleRCRS" +
      ".Check\032\026.simpleRCRS.ActionType\"\000\0220\n\007SetM" +
      "ove\022\021.simpleRCRS.Check\032\020.simpleRCRS.Move" +
      "\"\000\022C\n\013RunTimestep\022\032.simpleRCRS.WorldInfo" +
      "Proto\032\026.simpleRCRS.ActionType\"\000B(\n\022adf.s" +
      "ample.tacticsB\020firebrigadeProtoP\001b\006proto" +
      "3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_simpleRCRS_ActionType_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_simpleRCRS_ActionType_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_simpleRCRS_ActionType_descriptor,
        new java.lang.String[] { "ActionType", "X", "Y", });
    internal_static_simpleRCRS_Move_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_simpleRCRS_Move_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_simpleRCRS_Move_descriptor,
        new java.lang.String[] { "X", "Y", });
    internal_static_simpleRCRS_Check_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_simpleRCRS_Check_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_simpleRCRS_Check_descriptor,
        new java.lang.String[] { "Check", });
    internal_static_simpleRCRS_EdgeProto_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_simpleRCRS_EdgeProto_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_simpleRCRS_EdgeProto_descriptor,
        new java.lang.String[] { "StartX", "StartY", "EndX", "EndY", "Neighbour", });
    internal_static_simpleRCRS_AreaProto_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_simpleRCRS_AreaProto_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_simpleRCRS_AreaProto_descriptor,
        new java.lang.String[] { "URN", "ID", "X", "Y", "Edges", "Blockades", "ApexList", "Neighbours", "Floors", "Ignition", "Fieryness", "Brokenness", "BuildingCode", "BuildingAttributes", "GroundArea", "TotalArea", "Temperature", "Importance", "IsOnFire", });
    internal_static_simpleRCRS_HumanProto_descriptor =
      getDescriptor().getMessageTypes().get(5);
    internal_static_simpleRCRS_HumanProto_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_simpleRCRS_HumanProto_descriptor,
        new java.lang.String[] { "URN", "ID", "X", "Y", "PositionID", "PositionHistory", "TravelDistance", "Buriedness", "Damage", "HP", "Stamina", "Direction", "Water", });
    internal_static_simpleRCRS_BlockadeProto_descriptor =
      getDescriptor().getMessageTypes().get(6);
    internal_static_simpleRCRS_BlockadeProto_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_simpleRCRS_BlockadeProto_descriptor,
        new java.lang.String[] { "URN", "ID", "X", "Y", "Cost", "PositionID", "ApexList", });
    internal_static_simpleRCRS_ElseProto_descriptor =
      getDescriptor().getMessageTypes().get(7);
    internal_static_simpleRCRS_ElseProto_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_simpleRCRS_ElseProto_descriptor,
        new java.lang.String[] { "URN", "ID", });
    internal_static_simpleRCRS_WorldInfoProto_descriptor =
      getDescriptor().getMessageTypes().get(8);
    internal_static_simpleRCRS_WorldInfoProto_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_simpleRCRS_WorldInfoProto_descriptor,
        new java.lang.String[] { "Areas", "Humans", "Blockades", "Elses", "Time", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
