package org.andreaiacono.moviecatalog

import org.andreaiacono.moviecatalog.service.NasService
import org.junit.Test


class NasServiceTest {
    @Test
    fun parsing() {

        val xmlContent = """
            <?xml version="1.0"  encoding="UTF-8"?>
                <details>
                    <nasMovie xmlns:xsi="http:--www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http:--www.w3.org/2001/XMLSchema">
                        <title>300</title>
                        <year>2007</year>
                        <rating>66</rating>
                        <plot>Re Leonida - malgrado gli Efori (interpreti dei segni dell'Oracolo) gli dicano di non scendere in guerra contro Serse e l'esercito persiano, per evitare che il suo popolo, Sparta ed i suoi cari vengano fatti schiavi - decide comunque di partire, portando con sé 300 valorosi spartani nel tentativo disperato di fermare l'avanzata dell'invasore nella 'strettoia' delle Termopili...</plot>
                        <date>06/09/2013</date>
                        <runtime>117 min</runtime>
                        <genres>
                            <genre>Azione</genre>
                            <genre>Storia</genre>
                            <genre>Guerra</genre>
                        </genres>
                        <director>
                         <name>Zack Snyder</name>
                        </director>
                        <cast>
                            <actor>Gerard Butler</actor>
                            <actor>Lena Headey</actor>
                            <actor>Kwasi Songui</actor>
                            <actor>Alexandra Beaton</actor>
                            <actor>Frédéric Smith</actor>
                        </cast>
                        <filelink>Please enter the Dune path/300</filelink>
                    </nasMovie>
                </details>
        """.trim()

//        val movie = NasService("", "", "").getAllTitles(xmlContent, "fooDir")
//        assertEquals("300", movie.title)
//        assertEquals("fooDir", movie.nasDirName)
//        assertEquals("06/09/2013", SimpleDateFormat("dd/MM/yyyy").format(movie.date))
    }


    @Test
    fun loading() {
        val service = NasService("smb://192.168.1.90/Volume_1/movies/")
//        print(service.getFullImage()
//        print(service.getAllTitles().joinToString { it.toString() })
    }


}
