package br.com.avinfo.avboleto.sql;

public class Queries {
	
	public static final String FIND_CEDENTE =
			"SELECT"
			+ " razao, "
			+ " fantasia, "
			+ " cgc, endereco, "
			+ " numend, "
			+ " null, "
			+ " bairro, "
			+ " c.cep, "
			+ " t.codigo, "
			+ " telefone, "
			+ " email "
			+ "FROM controle c "
			+ "left join tabmu01i t on t.cidade = c.cidade ";
	
	public static final String FIND_BOLETOS = 
			  "SELECT "
			+ "   bol.id             BoletoId                  ,"      //retirar digito
			+ "   cc.numcon30        CedenteContaNumero        ,"      //retirar digito
			+ "   cc.numcon30        CedenteContaNumeroDV      ,"      //pegar somente digito
			+ "   cv.CodBeneficiario CedenteConvenioNumero     ,"
			+ "   ban.cod40           CedenteContaCodigoBanco   ," 
			+ "   cli.cgcclie1       SacadoCPFCNPJ             ,"  
			+ "   cli.eleclie1       SacadoEmail               ,"  
			+ "   cli.numend01       SacadoEnderecoNumero      ,"  
			+ "   cli.baiclie1       SacadoEnderecoBairro      ,"  
			+ "   cli.cepclie1       SacadoEnderecoCEP         ,"  
			+ "   cli.cidclie1       SacadoEnderecoCidade      ,"  
			+ "   ''                 SacadoEnderecoComplemento ,"      //null
			+ "   cli.endclie1       SacadoEnderecoLogradouro  ," 
			+ "   'Brasil'           SacadoEnderecoPais        ,"   
			+ "   cli.estclie1       SacadoEnderecoUF          ,"   
			+ "   cli.nomclie1       SacadoNome                ,"   
			+ "   cli.telclie1       SacadoTelefone            ,"   
			+ "   cli.celclie1       SacadoCelular             ,"   
			+ "   bol.DataEmissao    TituloDataEmissao         ,"     //converter Data dd/MM/yyyy
			+ "   bol.DataVencimento TituloDataVencimento      ,"     //converter Data dd/MM/yyyy 
			+ "   bol.Mensagem01     TituloMensagem01          ,"     
			+ "   bol.Mensagem02     TituloMensagem02          ,"      
			+ "   ''                 TituloMensagem03          ,"     //null
			+ "   bol.NossoNumero    TituloNossoNumero         ,"  
			+ "   bol.NumeroBoleto   TituloNumeroDocumento     ,"  
			+ "   bol.Valor          TituloValor               ,"  
			+ "   bol.LocalPagamento TituloLocalPagamento       " 
			+ "FROM boleto bol "
			+ "LEFT JOIN statusboleto sb on sb.id = bol.id "
			+ "LEFT JOIN conta30i cc on cc.sr_recno = bol.idconta30i "
			+ "LEFT JOIN banco40i ban ON ban.codban40 = cc.numban30 "
			+ "LEFT JOIN clien01i cli on cli.codclie1 = bol.CodCliente "
			+ "LEFT JOIN convenio cv on cv.id = bol.ConvenioID "
			+ "WHERE bol.NumeroBoleto = :#${body[param1]} "
			+ "ORDER BY bol.id ";
	
	
	public static final String UPDATE_STATUS_BOLETO_BY_NUMERO_DOCUMENTO = 
			  "UPDATE"
			+ "   statusboleto sb SET situacao =   :#${property.situacaoId}  , "
			+ "                    descricao=      :#${property.situacao}    , "
			+ "                    mensagem=       :#${property.mensagem}    , "
			+ "                    id_integracao=  :#${property.idIntegracao}  "
			+ "   WHERE sb.Id = (SELECT Id FROM boleto WHERE NumeroBoleto = :#${property.numeroDocumento} and NossoNumero = :#${property.nossoNumero}) ";


	public static final String UPDATE_STATUS_BOLETO_BY_IDS =
			  "UPDATE"
			+ "   statusboleto sb SET situacao =   :#${property.situacaoId}  , "
			+ "                    descricao=      :#${property.situacao}    , "
			+ "                    mensagem=       :#${property.mensagem}    , "
			+ "                    id_integracao=  :#${property.idIntegracao}  "
			+ "   WHERE sb.id IN (:#in:ids_filtrados)";
	
	
	public static final String FIND_IDS_INTEGRACAO_BOLETOS_BY_SITUACAO_AND_IDS = 
			"SELECT id_integracao FROM statusboleto sb where sb.situacao = :#${property.situacaoId} and sb.id IN (:#in:ids_filtrados)";

	public static final String FIND_IDS_INTEGRACAO_BOLETOS_DIF_ERRO = 
			"SELECT id_integracao FROM statusboleto sb where sb.situacao != 3 and sb.id IN (:#in:ids_filtrados)";
	
	public static final String FIND_IDS_INTEGRACAO_BOLETOS_PARA_GERAR_REMESSA =
			"SELECT" 
			+ " sb.id_integracao id_integracao FROM statusboleto sb "
			+ " LEFT JOIN boleto bol on bol.id = sb.id "
			+ " WHERE bol.NossoNumero IN ( :#in:nossos_numeros ) and sb.situacao IN (4, 5, 7, 8, 9) ";  //EMITIDO, PROTOCOLADO, REGISTRADO, LIQUIDADO, BAIXADO
			
	

	public static final String FIND_IDS_INTEGRACAO_BOLETOS_BY_SITUACAO =
			"SELECT id_integracao FROM statusboleto sb where sb.situacao = :#${property.situacaoId}";
	
	public static final String INSERT_BOLETO_PROTOCOLO = 
			"INSERT INTO boletoprotocolo (protocolo) VALUES ( :#${property.protocolo} )";
	
	public static final String UPDATE_STATUS_BOLETO_PROTOCOLO_BY_IDS = 
			"UPDATE"
			+ " statusboleto sb SET protocolo = :#${property.protocolo}  ,"
			+ "                     mensagem  = :#${property.mensagem}   ,"
			+ "                     situacao  = :#${property.situacaoId} ," 
			+ "                     descricao = :#${property.situacao}    " 
			+ " WHERE sb.id_integracao IN (:#in:ids_filtrados) or sb.id IN (:#in:ids_filtrados)";
	
	public static final String UPDATE_BOLETO_PROTOCOLO_PDF = 
			"UPDATE"
			+ " boletoprotocolo SET arquivo = :#${body} "
			+ " WHERE protocolo = :#${property.protocolo} ";


	public static final String FIND_COMMANDOS = 
			"SELECT id, comando, param1 FROM boletocomando WHERE status = 1 order by id";


	public static final String UPDATE_COMANDO_STATUS = 
			"UPDATE"
			+ " boletocomando SET status = :#${header.comando-status}, mensagem = :#${header.comando-mensagem} WHERE id IN ( :#in:comando_ids ) ";


	public static final String INSERT_REMESSA = 
			"INSERT"
			+ " INTO remessa (nome_arquivo, arquivo) VALUES ( :#${body[arquivo]}, :#${body[remessa]} ) ";
	
	public static final String UPDATE_STATUS_BOLETO_REMESSA = 
			"UPDATE"
			+ " statusboleto sb SET remessa_id = (SELECT rem.id FROM remessa rem WHERE rem.nome_arquivo = :#${body[arquivo]}) "
			+ " WHERE sb.id_integracao IN ( :#in:ids_integracao ) ";

	public static final String FIND_CONTA_BY_ID = 
			   " SELECT "
			+  " cod40 ContaCodigoBanco, "
			+  " numage30 ContaAgencia, "
			+  " numage30 ContaAgenciaDV, "
			+  " numCon30 ContaNumero, "
			+  " numCon30 ContaNumeroDV, "
			+  " tpConta30 ContaTipo, "
			+  " codBeneficiario30 ContaCodigoBeneficiario "
			+  " FROM conta30i cc "
			+  " LEFT JOIN banco40i ban ON ban.codban40 = cc.numban30 "
			+  " WHERE cc.sr_recno = :#${body[param1]} ";

	public static final String FIND_CNPJ_CONTROLE = 
			"SELECT cgc FROM controle c";

	public static final String FIND_CONVENIO = 
			   " SELECT "
			+  " CodBeneficiario ConvenioNumero, "
			+  " Descricao ConvenioDescricao, "
			+  " Carteira ConvenioCarteira, "
			+  " Especie ConvenioEspecie, "
			+  " PadraoCnab ConvenioPadraoCNAB,"
			+  " NumeroRemessa ConvenioNumeroRemessa, "
			+  " 'false' ConvenioReiniciarDiariamente, "
			+  " co.IdIntegracao Conta "
			+  " FROM convenio cv "
			+  " LEFT JOIN conta30i co ON co.sr_recno = cv.ContaId "
			+  " WHERE cv.Id = :#${body[param1]}";
	
}
