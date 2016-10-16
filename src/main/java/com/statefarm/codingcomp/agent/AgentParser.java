package com.statefarm.codingcomp.agent;

import com.statefarm.codingcomp.bean.*;
import com.statefarm.codingcomp.utilities.SFFileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashSet;

@Component
public class AgentParser {
	@Autowired
	private SFFileReader sfFileReader;

	@Cacheable(value = "agents")
	public Agent parseAgent(String fileName) {
		File f = new File(fileName);
		FileReader filereader;
		BufferedReader br;
		String line;
		try {
			filereader = new FileReader(fileName);
		} catch (FileNotFoundException ex) {
			return null;
		}
		if (filereader != null) {
			br = new BufferedReader(filereader);
		} else {
			return null;
		}

		Agent smith = new Agent();
		int namefound = 0;
		int addr = 0;
		int officeHours = 0;
		int officePhone = 0;
		int language = 0;
		int proj = 0;
		try {
			Office office = new Office();
			while ((line = br.readLine()) != null && proj == 0) {
				if (namefound == 0) {
					if (!line.contains("<span itemprop=\"name\">")) {
						continue;
					}
					int nameIndex = line.indexOf("<span itemprop=\"name\">");
					nameIndex += "<span itemprop=\"name\">".length();
					String name = line.substring(nameIndex).split("<")[0];
					smith.setName(name);
					namefound = 1;
					continue;
				} else if (addr == 0) {
					if (!line.contains("streetAddress")) {
						continue;
					}
					Address officeAddress = new Address();
					line = br.readLine();
					line = br.readLine();
					String[] address = new String[2];
					if (line.contains("<br>")) {
						address = line.split("<br>");
						officeAddress.setLine1(address[0]);
						officeAddress.setLine2(address[1]);
					} else {
						officeAddress.setLine1(line.replaceAll("\t", " ").trim());
					}
					line = br.readLine();
					while (!line.contains("<span itemprop=")) {
						line = br.readLine();
					}
					int nameIndex = line.indexOf("<span itemprop=\"addressLocality\">")
							+ "<span itemprop=\"addressLocality\">".length();
					officeAddress.setCity(line.substring(nameIndex).split(",")[0]);
					line = br.readLine();
					nameIndex = line.indexOf("<span itemprop=\"addressRegion\">")
							+ "<span itemprop=\"addressRegion\">".length();
					USState state = USState.fromValue(line.substring(nameIndex).split("<")[0].trim());
					officeAddress.setState(state);
					line = br.readLine();
					nameIndex = line.indexOf("<span itemprop=\"postalCode\">")
							+ "<span itemprop=\"postalCode\">".length();
					officeAddress.setPostalCode(line.substring(nameIndex).split("<")[0]);
					office.setAddress(officeAddress);
					addr = 1;
//				} else if (officePhone == 0) {
//					if (!line.contains("Office Phone")) {
//						continue;
//					}
//					String phonenumber = line.split("<span>")[1].split("</span>")[0];
//				} else if (officeHours == 0){
//
//				} else if (language == 0) {

				} else if (proj == 0) {
					if (!line.contains("<div itemprop=description   >")) {
						continue;
					}
					int nameIndex2 = line.indexOf("<div itemprop=description   ><ul><li>");
					nameIndex2 += "<div itemprop=description   ><ul><li>".length();
					String[] name = line.substring(nameIndex2).split("</li><li>");
					name[name.length - 1] = name[name.length - 1].substring(0,
							name[(name.length) - 1].indexOf("</li></ul></div itemprop=description>"));
					HashSet<Product> products = new HashSet<>();
					for (String x : name) {
						products.add(Product.fromValue(x.trim()));
					}
					smith.setProducts(products);
					break;
				}
			}
		} catch (IOException e) {
		}
		return smith;
	}
}
