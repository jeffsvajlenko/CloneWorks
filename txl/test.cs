<source file="systems/castle/src/Castle.Windsor/Windsor/Configuration/Interpreters/XmlInterpreter.cs" startline="134" endline="146">
                private static void DeserializeComponents(XmlNodeList nodes, IConfigurationStore store, IConversionManager converter)
                {
                        foreach (XmlNode node in nodes)
                        {
                                if (node.NodeType != XmlNodeType.Element)
                                {
                                        continue;
                                }

                                AssertNodeName(node, ComponentNodeName);
                                DeserializeComponent(node, store, converter);
                        }
                }
</source>
<source file="systems/castle/src/Castle.Windsor/Windsor/Configuration/Interpreters/XmlInterpreter.cs" startline="211" endline="223">
                private static void DeserializeFacilities(XmlNodeList nodes, IConfigurationStore store, IConversionManager converter)
                {
                        foreach (XmlNode node in nodes)
                        {
                                if (node.NodeType != XmlNodeType.Element)
                                {
                                        continue;
                                }

                                AssertNodeName(node, FacilityNodeName);
                                DeserializeFacility(node, store, converter);
                        }
                }
</source>
