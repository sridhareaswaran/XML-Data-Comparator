package com.sri.comparator;

import java.io.File;

import Fillo.Connection;
import Fillo.Fillo;
import Fillo.Recordset;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


/**
 * Created by easwarsx on 10/28/2015.
 */

public class checkExcelData {
    String Summary;

    public checkExcelData(){}

    public checkExcelData(String file_name, String masterdir)
    {
        Summary=verifyData(file_name,masterdir);
    }

    public String getSummary()
    {
        return this.Summary;
    }



    public static String verifyData(String file_name, String masterdir) {

        long start_time=System.nanoTime();
        int tcount=0,file_count=0,pass=0,fail=0;
        String cleanq,dataq,q;
        String c_value,r_value;
        String Summary1="try closing and opening the comparator tool !!";
        String[] slno = new String[1000];
        String[] c_name = new String[1000];
        String[] r_name = new String[1000];
        String[] c_tag = new String[1000];
        String[] r_tag= new String[1000];

        try
        {
            Fillo fillo = new Fillo();
            Connection con = fillo.getConnection(file_name);
            //clean data sheet
            cleanq = "Update Data Set Status='',Log='',C_Value='',R_Value='' where C_Name!='' and R_Name!=''";
            con.executeUpdate(cleanq);
            //total data query
            dataq="Select * from Data where C_Name!='' and R_Name!=''";
            Recordset rset=con.executeQuery(dataq);

            int i=1;
            tcount=rset.getCount();

            while (rset.next())
            {
                slno[i]=rset.getField("Sl_No");
                c_name[i]=rset.getField("C_Name").trim();
                r_name[i]=rset.getField("R_Name").trim();
                c_tag[i]=rset.getField("C_Tag").trim();
                r_tag[i]=rset.getField("R_Tag").trim();
                i++;
            }

            con.close();

            Set c_set = new HashSet(Arrays.asList(c_name));
            Set r_set = new HashSet(Arrays.asList(r_name));
            file_count = (c_set.size() + r_set.size()) - 2;

            //comparision starts here
            for(i=1;i <=tcount;i++)
            {
                q = "Update Data Set Status='Fail',Log='Not compared yet' where Sl_No=" + slno[i];
                Fillo fill = new Fillo();
                Connection conn = fillo.getConnection(file_name);
                File c_file=new File(masterdir + File.separator + "controller" + File.separator + c_name[i] + ".xml");
                File r_file=new File(masterdir + File.separator + "receiver" + File.separator + r_name[i] + ".xml");

                if (!c_file.isFile())
                {
                    fail++;
                    q="Update Data Set Status='Fail',Log='C_file missing or might be a naming conflict' where Sl_No="+slno[i];
                }
                else if(!r_file.isFile())
                {
                    fail++;
                    q="Update Data Set Status='Fail',Log='R_file missing or might be a naming conflict' where Sl_No="+slno[i];
                }
                else
                {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document c_doc= db.parse(c_file);
                    Document r_doc= db.parse(r_file);

                    XPathFactory xpathFactory = XPathFactory.newInstance();
                    XPath xpath = xpathFactory.newXPath();
//                    XPathExpression c_expr = xpath.compile("//*[local-name() = '" + c_tag[i] + "']/text()");
//                    XPathExpression r_expr = xpath.compile("//*[local-name() = '" + r_tag[i] + "']/text()");
                    XPathExpression c_expr = xpath.compile("//*[local-name() = '" + c_tag[i] + "']");
                    XPathExpression r_expr = xpath.compile("//*[local-name() = '" + r_tag[i] + "']");
                    Object c_node = c_expr.evaluate(c_doc, XPathConstants.NODESET);
                    NodeList c_nodes = (NodeList) c_node;
                    Object r_node = r_expr.evaluate(r_doc, XPathConstants.NODESET);
                    NodeList r_nodes = (NodeList) r_node;


                    if(c_nodes.getLength()>0 && r_nodes.getLength()>0)
                    {
                        c_value=c_nodes.item(0).getTextContent();
                        r_value=r_nodes.item(0).getTextContent();
                        System.out.println(c_value+"\t"+r_value);
                        System.out.println(i);

                        if(Objects.equals(c_value,r_value))
                        {
                            pass++;
                            q="Update Data Set Status='Pass',Log=':)',C_Value='"+c_value +"',R_Value='"+ r_value +"' where Sl_No="+slno[i];
                        }
                        else
                        {
                            fail++;
                            q="Update Data Set Status='Fail',Log='Miss match found',C_Value='"+c_value +"',R_Value='"+ r_value +"' where Sl_No="+slno[i];
                        }
                    }
                    else if(c_nodes.getLength()==0 && r_nodes.getLength()==0)
                    {
                        fail++;
                        q="Update Data Set Status='Fail',Log='Both C_Tag & R_Tag are Missing' where Sl_No="+slno[i];
                    }
                    else if(c_nodes.getLength()==0 && r_nodes.getLength()>0)
                    {
                        fail++;
                        r_value=r_nodes.item(0).getTextContent();
                        q="Update Data Set Status='Fail',Log='C_Tag Missing',R_Value='"+ r_value +"' where Sl_No="+slno[i];
                    }
                    else if(c_nodes.getLength()>0 && r_nodes.getLength()==0)
                    {
                        fail++;
                        c_value=c_nodes.item(0).getTextContent();
                        q="Update Data Set Status='Fail',Log='R_Tag Missing',C_Value='"+ c_value +"' where Sl_No="+slno[i];
                    }



                }

                conn.executeUpdate(q);
                System.out.println(q);
                c_file=null;
                r_file=null;
                c_value="";
                r_value="";
                Thread.sleep(177);
            }

            //Build Summary
            long end_time=System.nanoTime();
            long elapsed=end_time-start_time;
            double seconds=(double)elapsed/1000000000.0;
            double sec = Math.round(seconds*100);
            sec=sec/100;
            Summary1= "Process Summary: \n\n"
                    + "No of files Processed : " + file_count +"\n"
                    + "No of Data Validated  : " + tcount*2 +"\n"
                    + "Passed                : " + pass +"\n"
                    + "Failed                : " + fail +"\n\n"
                    + "Time taken            : " + sec + " Sec\n";


        }
        catch (Exception e2) { e2.printStackTrace(); }

        return(Summary1);


    }
}