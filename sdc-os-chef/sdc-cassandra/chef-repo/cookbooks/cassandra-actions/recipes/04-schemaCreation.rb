cookbook_file "/tmp/sdctool.tar" do
  source "sdctool.tar"
  mode 0755
end

## extract sdctool.tar
bash "install tar" do
  cwd "/tmp"
  code <<-EOH
     /bin/tar xf /tmp/sdctool.tar -C /tmp
  EOH
end


template "titan.properties" do
  sensitive true
  path "/tmp/sdctool/config/titan.properties"
  source "titan.properties.erb"
  mode "0755"
  variables({
      :DC_NAME      => node['cassandra'][:cluster_name]+node.chef_environment
  })
end


template "/tmp/sdctool/config/configuration.yaml" do
  sensitive true
  source "configuration.yaml.erb"
  mode 0755
  variables({
      :host_ip      => node['HOST_IP'],
      :catalog_port => node['BE'][:http_port],
      :ssl_port     => node['BE'][:https_port],
      :cassandra_ip => node['Nodes']['CS'],
      :rep_factor   => 1,
      :DC_NAME      => node['cassandra'][:cluster_name]+node.chef_environment,
      :titan_Path   => "/tmp/sdctool/config/"
  })
end


template "/tmp/sdctool/config/elasticsearch.yml" do
  sensitive true
  source "elasticsearch.yml.erb"
  mode 0755
  variables({
     :elastic_ip => "HOSTIP"    
  })
end


bash "excuting-schema-creation" do
   code <<-EOH
     cd /tmp
     chmod +x /tmp/sdctool/scripts/schemaCreation.sh
     /tmp/sdctool/scripts/schemaCreation.sh /tmp/sdctool/config
   EOH
end
